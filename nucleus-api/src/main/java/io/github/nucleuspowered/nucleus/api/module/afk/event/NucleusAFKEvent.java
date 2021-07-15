/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.afk.event;

import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.message.AudienceMessageEvent;
import org.spongepowered.api.event.message.MessageEvent;

import java.util.UUID;

public interface NucleusAFKEvent extends MessageEvent, AudienceMessageEvent, Event {

    /**
     * The {@link UUID} of the player whose AFK status is being inspected or
     * updated.
     *
     * @return The {@link UUID} of the player.
     */
    UUID getTargetPlayer();

    /**
     * Fired when a player goes AFK.
     *
     * <p>
     *     <strong>This event might fire async!</strong>
     * </p>
     */
    @MightOccurAsync
    interface GoingAFK extends NucleusAFKEvent {}

    /**
     * Fired when a player returns from AFK.
     *
     * <p>
     *     <strong>This event might fire async!</strong>
     * </p>
     */
    @MightOccurAsync
    interface ReturningFromAFK extends NucleusAFKEvent {}

    /**
     * Fired when a player is about to be kicked due to inactivity.
     *
     * <p>
     *     If this event is cancelled, the player will not be kicked for
     *     inactivity until the player comes back from AFK and goes AFK again.
     * </p>
     * <p>
     *     <strong>This event might fire async!</strong>
     * </p>
     */
    @MightOccurAsync
    interface Kick extends NucleusAFKEvent, Cancellable {}

    /**
     * Fired when the target of a command is AFK and the command is marked
     * as one that should notify the sender.
     */
    @MightOccurAsync
    interface NotifyCommand extends NucleusAFKEvent, MessageEvent, Event { }
}
