/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.staffchat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.message.AudienceMessageEvent;

import java.util.UUID;

/**
 * Provides a way to get the Staff Chat message channel instance.
 */
public interface NucleusStaffChatService {

    /**
     * Gets if the provided {@link AudienceMessageEvent} is going to be
     * sent to the staff chat channel.
     *
     * @param event The {@link AudienceMessageEvent}
     * @return true if so
     */
    boolean isDirectedToStaffChat(AudienceMessageEvent event);

    /**
     * Gets if the given {@link Audience} is currently talking
     * in staff chat.
     *
     * @param source The {@link Audience}
     * @return true if so
     */
    boolean isCurrentlyChattingInStaffChat(Audience source);

    /**
     * Gets if a player with the given {@link UUID} is currently talking
     * in staff chat.
     *
     * @param uuid The {@link UUID} of the {@link User}
     * @return true if so
     */
    boolean isCurrentlyChattingInStaffChat(UUID uuid);

    /**
     * Gets the members of the Staff Chat channel.
     *
     * @return The {@link Audience}s who are members of the channel.
     */
    Audience getStaffChannelMembers();

    /**
     * Sends a message to the Staff Chat channel.
     *
     * @param source The {@link Audience} that is sending this message.
     * @param message The message to send.
     */
    void sendMessageFrom(Audience source, Component message);

}
