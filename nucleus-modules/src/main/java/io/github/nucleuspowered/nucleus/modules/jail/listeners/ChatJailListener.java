/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.listeners;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.jail.config.JailConfig;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailHandler;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;

import com.google.inject.Inject;
import org.spongepowered.api.event.message.PlayerChatEvent;

public class ChatJailListener implements ListenerBase.Conditional {

    private final JailHandler handler;
    private final IMessageProviderService messageProviderService;

    @Inject
    public ChatJailListener(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(JailHandler.class);
        this.messageProviderService = serviceCollection.messageProvider();
    }

    @Listener(order = Order.FIRST)
    public void onChat(final PlayerChatEvent event, @Root final ServerPlayer player) {
        if (this.handler.checkJail(player.getUniqueId(), false)) {
            this.messageProviderService.sendMessageTo(player, "jail.muteonchat");
            event.setCancelled(true);
        }
    }

    @Override public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(JailConfig.class).isMuteOnJail();
    }

}
