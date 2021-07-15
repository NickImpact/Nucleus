/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.events;

import io.github.nucleuspowered.nucleus.api.module.kit.KitRedeemResult;
import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.api.module.kit.event.NucleusKitEvent;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public abstract class KitEvent extends AbstractEvent implements NucleusKitEvent {

    public static abstract class Redeem extends KitEvent implements NucleusKitEvent.Redeem {

        private final Cause cause;
        private final Kit kit;
        @Nullable private final Instant lastTime;
        private final ServerPlayer targetPlayer;
        private final Collection<ItemStackSnapshot> original;
        private final Collection<String> commands;

        public Redeem(final Cause cause, @Nullable final Instant lastTime, final Kit kit, final ServerPlayer targetPlayer,
                final Collection<ItemStackSnapshot> original,
                final Collection<String> commands) {
            this.cause = cause;
            this.kit = kit;
            this.targetPlayer = targetPlayer;
            this.lastTime = lastTime;
            this.original = original;
            this.commands = commands;
        }

        @Override public Optional<Instant> getLastRedeemedTime() {
            return Optional.ofNullable(this.lastTime);
        }

        @Override public String getName() {
            return this.kit.getName();
        }

        @Override public Kit getRedeemedKit() {
            return this.kit;
        }

        @Override public Collection<ItemStackSnapshot> getOriginalStacksToRedeem() {
            return this.original;
        }

        @Override public ServerPlayer getRedeemingPlayer() {
            return this.targetPlayer;
        }

        @Override public Cause cause() {
            return this.cause;
        }

        @Override public Collection<String> getOriginalCommandsToExecute() {
            return this.commands;
        }
    }

    public static class PreRedeem extends Redeem implements NucleusKitEvent.Redeem.Pre {

        @Nullable private Component cancelMessage = null;
        private boolean isCancelled;
        @Nullable private Collection<ItemStackSnapshot> toRedeem = null;
        @Nullable private Collection<String> commandRedeem = null;

        public PreRedeem(final Cause cause, @Nullable final Instant lastTime, final Kit kit, final ServerPlayer targetPlayer,
                final Collection<ItemStackSnapshot> original,
                final Collection<String> originalCommands) {
            super(cause, lastTime, kit, targetPlayer, original, originalCommands);
        }

        @Override public boolean isCancelled() {
            return this.isCancelled;
        }

        @Override public void setCancelled(final boolean cancel) {
            this.isCancelled = cancel;
        }

        @Override public Optional<Component> getCancelMessage() {
            return Optional.ofNullable(this.cancelMessage);
        }

        @Override public void setCancelMessage(@Nullable final Component message) {
            this.cancelMessage = message;
        }

        @Override public Optional<Collection<ItemStackSnapshot>> getStacksToRedeem() {
            return Optional.ofNullable(this.toRedeem);
        }

        @Override public void setStacksToRedeem(@Nullable final Collection<ItemStackSnapshot> stacksToRedeem) {
            if (stacksToRedeem == null) {
                this.toRedeem = null;
            } else {
                this.toRedeem = Collections.unmodifiableCollection(stacksToRedeem);
            }
        }

        @Override public Optional<Collection<String>> getCommandsToExecute() {
            return Optional.ofNullable(this.commandRedeem);
        }

        @Override public void setCommandsToExecute(@Nullable final Collection<String> commandsToExecute) {
            if (commandsToExecute == null) {
                this.commandRedeem = null;
            } else {
                this.commandRedeem = Collections.unmodifiableCollection(commandsToExecute);
            }
        }
    }

    public static class PostRedeem extends Redeem implements NucleusKitEvent.Redeem.Post {

        @Nullable private final Collection<ItemStackSnapshot> redeemed;
        @Nullable private final Collection<String> commands;

        public PostRedeem(final Cause cause, @Nullable final Instant lastTime, final Kit kit, final ServerPlayer targetPlayer,
                final Collection<ItemStackSnapshot> original,
                @Nullable final Collection<ItemStackSnapshot> redeemed, final Collection<String> originalCommands, @Nullable final Collection<String> commands) {
            super(cause, lastTime, kit, targetPlayer, original, originalCommands);
            this.redeemed = redeemed;
            this.commands = commands;
        }

        @Override public Optional<Collection<ItemStackSnapshot>> getStacksToRedeem() {
            return Optional.ofNullable(this.redeemed);
        }

        @Override public Optional<Collection<String>> getCommandsToExecute() {
            return Optional.ofNullable(this.commands);
        }
    }

    public static class FailedRedeem extends Redeem implements NucleusKitEvent.Redeem.Failed {

        @Nullable private final Collection<ItemStackSnapshot> redeemed;
        @Nullable private final Collection<String> commands;
        private final KitRedeemResult.Status ex;

        public FailedRedeem(final Cause cause, @Nullable final Instant lastTime, final Kit kit, final ServerPlayer targetPlayer,
                final Collection<ItemStackSnapshot> original,
                @Nullable final Collection<ItemStackSnapshot> redeemed, final Collection<String> originalCommands, @Nullable final Collection<String> commands,
                final KitRedeemResult.Status ex) {
            super(cause, lastTime, kit, targetPlayer, original, originalCommands);
            this.redeemed = redeemed;
            this.commands = commands;
            this.ex = ex;
        }

        @Override public Optional<Collection<ItemStackSnapshot>> getStacksToRedeem() {
            return Optional.ofNullable(this.redeemed);
        }

        @Override public Optional<Collection<String>> getCommandsToExecute() {
            return Optional.ofNullable(this.commands);
        }

        @Override public KitRedeemResult.Status getReason() {
            return this.ex;
        }
    }
}
