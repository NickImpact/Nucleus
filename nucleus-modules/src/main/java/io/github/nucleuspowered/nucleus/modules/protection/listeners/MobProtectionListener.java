/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.protection.listeners;

import io.github.nucleuspowered.nucleus.modules.protection.config.ProtectionConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.block.transaction.Operation;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MobProtectionListener implements IReloadableService.Reloadable, ListenerBase.Conditional {

    private List<EntityType<?>> whitelistedTypes;

    @Listener
    public void onMobChangeBlock(final ChangeBlockEvent.All event, @Root final Living living) {
        if (living instanceof ServerPlayer || this.whitelistedTypes.contains(living.type())) {
            return;
        }

        event.transactions().stream().filter(x -> {
            final Operation operation = x.operation();
            return operation != Operations.GROWTH.get() && operation != Operations.DECAY.get();
        }).forEach(x -> x.setValid(false));
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.whitelistedTypes = serviceCollection.configProvider().getModuleConfig(ProtectionConfig.class)
                .getWhitelistedEntities()
                .stream()
                .map(x -> RegistryTypes.ENTITY_TYPE.get().findValue(x).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(ProtectionConfig.class).isEnableProtection();
    }
}
