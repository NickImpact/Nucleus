/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat.services;

import io.github.nucleuspowered.nucleus.api.module.staffchat.NucleusStaffChatService;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatKeys;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatMessageChannel;
import io.github.nucleuspowered.nucleus.core.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.core.scaffold.service.annotations.APIService;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IChatMessageFormatterService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IUserPreferenceService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.message.AudienceMessageEvent;

import java.util.UUID;

import com.google.inject.Inject;

@APIService(NucleusStaffChatService.class)
public class StaffChatService implements NucleusStaffChatService, ServiceBase {

    private final IUserPreferenceService userPreferenceService;
    private final IChatMessageFormatterService chatMessageFormatService;

    @Inject
    public StaffChatService(final INucleusServiceCollection serviceCollection) {
        this.userPreferenceService = serviceCollection.userPreferenceService();
        this.chatMessageFormatService = serviceCollection.chatMessageFormatter();
    }

    @Override
    public void sendMessageFrom(final Audience source, final Component message) {
        StaffChatMessageChannel.getInstance().sendMessageFrom(source, message);
    }

    @Override
    public boolean isDirectedToStaffChat(final AudienceMessageEvent event) {
        final Object root = event.cause().root();
        if (root instanceof Audience) {
            return this.isCurrentlyChattingInStaffChat((Audience) root);
        }
        return false;
    }

    @Override
    public boolean isCurrentlyChattingInStaffChat(final Audience source) {
        return this.chatMessageFormatService.getNucleusChannel(source).filter(x -> x instanceof StaffChatMessageChannel).isPresent();
    }

    @Override
    public boolean isCurrentlyChattingInStaffChat(final UUID uuid) {
        return Sponge.server().player(uuid).map(this::isToggledChat).orElse(false);
    }

    @Override
    public Audience getStaffChannelMembers() {
        return StaffChatMessageChannel.getInstance().receivers();
    }

    public boolean isToggledChat(final Player player) {
        return this.chatMessageFormatService.getNucleusChannel(player.uniqueId()).filter(x -> x instanceof StaffChatMessageChannel).isPresent();
    }

    public void toggle(final ServerPlayer player, final boolean toggle) {
        if (toggle) {
            this.chatMessageFormatService.setPlayerNucleusChannel(player.uniqueId(), StaffChatMessageChannel.getInstance());

            // If you switch, you're switching to the staff chat channel so you should want to listen to it.
            this.userPreferenceService.setPreferenceFor(player.uniqueId(), StaffChatKeys.VIEW_STAFF_CHAT, true);
        } else {
            this.chatMessageFormatService.setPlayerNucleusChannel(player.uniqueId(), null);
        }
    }
}
