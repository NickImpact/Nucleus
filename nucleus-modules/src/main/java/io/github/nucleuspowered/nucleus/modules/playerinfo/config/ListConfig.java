/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ConfigSerializable
public class ListConfig {

    @Setting("list-grouping-by-permission")
    private GroupConfig groupByPermissionGroup = new GroupConfig();

    @Setting(value = "server-panel-compatibility")
    @LocalisedComment("config.playerinfo.list.panel")
    private boolean panelCompatibility = false;

    @Setting(value = "template")
    @LocalisedComment("config.playerinfo.list.template")
    private String template = "{{displayname}}";

    @Setting(value = "compact-list")
    @LocalisedComment("config.playerinfo.list.compact")
    private boolean compact = true;

    @Setting(value = "compact-max-players")
    @LocalisedComment("config.playerinfo.list.compactmax")
    private int maxPlayersPerLine = 20;

    public boolean isGroupByPermissionGroup() {
        return this.groupByPermissionGroup.enabled;
    }

    public List<String> getOrder() {
        return Collections.unmodifiableList(this.groupByPermissionGroup.groupPriority);
    }

    public String getDefaultGroupName() {
        if (this.groupByPermissionGroup.defaultGroupName.isEmpty()) {
            return "Default";
        }

        return this.groupByPermissionGroup.defaultGroupName;
    }

    public boolean isPanelCompatibility() {
        return this.panelCompatibility;
    }

    public String getListTemplate() {
        return this.template;
    }

    public boolean isCompact() {
        return this.compact;
    }

    public int getMaxPlayersPerLine() {
        return Math.max(1, this.maxPlayersPerLine);
    }

    @ConfigSerializable
    public static class GroupConfig {

        @Setting(value = "enabled")
        @LocalisedComment("config.playerinfo.list.groups")
        private boolean enabled = false;

        @Setting(value = "group-order")
        @LocalisedComment("config.playerinfo.list.grouporder")
        private List<String> groupPriority = new ArrayList<>();

        @Setting(value = "default-group-name")
        @LocalisedComment("config.playerinfo.list.defaultname")
        private String defaultGroupName = "Default";
    }
}
