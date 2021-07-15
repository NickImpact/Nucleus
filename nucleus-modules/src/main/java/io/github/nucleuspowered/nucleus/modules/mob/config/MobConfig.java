/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ConfigSerializable
public class MobConfig {

    @Setting(value = "max-mobs-to-spawn")
    @LocalisedComment("config.mobspawn.maxamt")
    private int maxMobsToSpawn = 20;

    @Setting(value = "spawning-blocks")
    @LocalisedComment("config.blockspawn.category")
    private Map<String, BlockSpawnsConfig> blockSpawnsConfig;

    @Setting(value = "separate-mob-spawning-permissions")
    @LocalisedComment("config.mobspawn.permob")
    private boolean perMobPermission = false;

    public MobConfig() {
        this.blockSpawnsConfig = new HashMap<>();
        this.blockSpawnsConfig.put("world", new BlockSpawnsConfig());
        this.blockSpawnsConfig.put("DIM-1", new BlockSpawnsConfig());
        this.blockSpawnsConfig.put("DIM1", new BlockSpawnsConfig());
    }

    public int getMaxMobsToSpawn() {
        return Math.max(1, this.maxMobsToSpawn);
    }

    public Map<String, BlockSpawnsConfig> getBlockSpawnsConfig() {
        return Collections.unmodifiableMap(this.blockSpawnsConfig);
    }

    public Optional<BlockSpawnsConfig> getBlockSpawnsConfigForWorld(final ServerWorld world) {
        return Optional.ofNullable(this.blockSpawnsConfig.get(world.key().asString()));
    }

    public boolean isPerMobPermission() {
        return this.perMobPermission;
    }
}
