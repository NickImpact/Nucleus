/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.services;

import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolKeys;
import io.github.nucleuspowered.nucleus.core.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IStorageManager;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.ItemType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;
import org.spongepowered.api.registry.RegistryTypes;

public class PowertoolService implements ServiceBase {

    private final Map<ItemType, String> cache = new HashMap<>();
    private final Map<UUID, Map<String, List<String>>> powertools = new HashMap<>();

    private final IStorageManager storageManager;

    @Inject
    public PowertoolService(final INucleusServiceCollection serviceCollection) {
        this.storageManager = serviceCollection.storageManager();
    }

    public Map<String, List<String>> getPowertools(final UUID uuid) {
        Map<String, List<String>> m = this.powertools.get(uuid);
        if (m == null) {
            // grab the user data
            m = this.storageManager.getUserService()
                    .getOrNewOnThread(uuid)
                    .get(PowertoolKeys.POWERTOOLS)
                    .orElseGet(HashMap::new);
            this.powertools.put(uuid, m);
        }

        return Collections.unmodifiableMap(m);
    }

    // TODO: Fix powertools - the module needs redoing to properly handle items types and not just do this
    //  hackjob right now
    private @Nullable String getKey(final ItemType type) {
        return this.cache.computeIfAbsent(type, t -> RegistryTypes.ITEM_TYPE.get().findValueKey(t).map(ResourceKey::asString).orElse(null));
    }

    public Optional<List<String>> getPowertoolForItem(final UUID uuid, final ItemType item) {
        final String key = this.getKey(item);
        if (key != null) {
            final List<String> tools = this.getPowertools(uuid).get(key);
            if (tools != null) {
                return Optional.of(Collections.unmodifiableList(tools));
            }
        }

        return Optional.empty();
    }

    public void setPowertool(final UUID uuid, final ItemType type, final List<String> commands) {
        final String key = this.getKey(type);
        if (key != null) {
            this.getPowertools(uuid).put(key, new ArrayList<>(commands));
            this.setBack(uuid);
        }
    }

    public void clearPowertool(final UUID uuid, final ItemType type) {
        final String key = this.getKey(type);
        if (key != null) {
            this.clearPowertool(uuid, key);
        }
    }

    public void clearPowertool(final UUID uuid, final String type) {
        this.getPowertools(uuid).remove(type);
        this.setBack(uuid);
    }

    public void reset(final UUID uuid) {
        this.powertools.remove(uuid);
        this.setBack(uuid);
    }

    private void setBack(final UUID uuid) {
        this.storageManager
                .getUserService()
                .getOrNew(uuid)
                .thenAccept(x -> x.set(PowertoolKeys.POWERTOOLS, this.powertools.getOrDefault(uuid, new HashMap<>())));
    }
}
