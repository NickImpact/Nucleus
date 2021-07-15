/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.afk.config.AFKConfig;
import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.Arrays;
import java.util.List;

public class AFKCommandListener extends AbstractAFKListener implements ListenerBase.Conditional {
    private final List<String> commands = Arrays.asList("afk", "away");

    @Inject
    public AFKCommandListener(final INucleusServiceCollection serviceCollection) {
        super(serviceCollection.getServiceUnchecked(AFKHandler.class));
    }

    @Listener
    public void onPlayerCommand(final ExecuteCommandEvent.Pre event, @Root final ServerPlayer player) {
        // Did the subject run /afk? Then don't do anything, we'll toggle it
        // anyway.
        if (!this.commands.contains(event.command().toLowerCase())) {
            this.update(player);
        }
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(AFKConfig.class)
                .getTriggers()
                .isOnCommand();
    }
}
