/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.protection.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ProtectionConfig {

    @Setting(value = "disable-crop-trample")
    @LocalisedComment("config.protection.disablecrop")
    private CropTrample disableCropTrample = new CropTrample();

    @Setting(value = "mob-griefing")
    private BlockBreaking blockBreaking = new BlockBreaking();

    public boolean isDisableAnyCropTrample() {
        return this.disableCropTrample.players || this.disableCropTrample.mobs;
    }

    public boolean isDisablePlayerCropTrample() {
        return this.disableCropTrample.players;
    }

    public boolean isDisableMobCropTrample() {
        return this.disableCropTrample.mobs;
    }

    public boolean isEnableProtection() {
        return this.blockBreaking.enableProtection;
    }

    public List<ResourceKey> getWhitelistedEntities() {
        return this.blockBreaking.whitelist;
    }

    @ConfigSerializable
    public static class CropTrample {

        @Setting
        private boolean players = false;

        @Setting
        private boolean mobs = false;
    }

    @ConfigSerializable
    public static class BlockBreaking {

        @Setting(value = "enable-protection")
        @LocalisedComment("config.protection.mobgriefing.flag")
        private boolean enableProtection = false;

        @Setting(value = "whitelist")
        private List<ResourceKey> whitelist = new ArrayList<>();


    }
}
