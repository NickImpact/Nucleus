/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class RepairConfig {

    @Setting(value = "use-whitelist")
    @LocalisedComment("config.item.repair.whitelist")
    private boolean useWhitelist = false;

    @Setting(value = "restrictions")
    @LocalisedComment("config.item.repair.restrictions")
    private List<ResourceKey> restrictions = new ArrayList<>();

    public boolean isWhitelist() {
        return this.useWhitelist;
    }

    public List<ResourceKey> getRestrictions() {
        return this.restrictions;
    }
}
