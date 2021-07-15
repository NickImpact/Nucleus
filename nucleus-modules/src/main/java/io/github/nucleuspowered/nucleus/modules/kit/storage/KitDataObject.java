/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.storage;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.serialiser.SingleKitTypeSerialiser;
import io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.configurate.AbstractConfigurateBackedDataObject;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KitDataObject extends AbstractConfigurateBackedDataObject implements IKitDataObject {

    private Map<String, Kit> cached;

    @Override
    public Map<String, Kit> getKitMap() {
        if (this.cached == null) {
            try {
                final Map<String, Kit> map = SingleKitTypeSerialiser.INSTANCE.deserialize(this.backingNode);
                if (map == null) {
                    this.cached = Collections.emptyMap();
                } else {
                    this.cached = Collections.unmodifiableMap(new HashMap<>(map));
                }
            } catch (final Exception e) {
                e.printStackTrace();
                return Collections.emptyMap();
            }
        }
        return this.cached;
    }

    @Override
    public void setKitMap(final Map<String, Kit> map) throws Exception {
        SingleKitTypeSerialiser.INSTANCE.serialize(map, this.backingNode);
        this.cached = Collections.unmodifiableMap(map);
    }

    @Override
    public boolean hasKit(final String name) {
        return this.getKitMap().containsKey(name.toLowerCase());
    }

    @Override
    public Optional<Kit> getKit(final String name) {
        return Optional.ofNullable(this.getKitMap().get(name.toLowerCase()));
    }

    @Override
    public void setKit(final Kit kit) throws Exception {
        final Map<String, Kit> m = new HashMap<>(this.getKitMap());
        m.put(kit.getName().toLowerCase(), kit);
        this.setKitMap(m);
    }

    @Override
    public boolean removeKit(final String name) throws Exception {
        final Map<String, Kit> m = new HashMap<>(this.getKitMap());
        final boolean b = m.remove(name.toLowerCase()) != null;
        this.setKitMap(m);
        return b;
    }

    @Override
    public void setBackingNode(final ConfigurationNode node) {
        super.setBackingNode(node);
        this.cached = null;
    }

}
