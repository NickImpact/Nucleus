/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fly.commands;

import io.github.nucleuspowered.nucleus.modules.fly.FlyKeys;
import io.github.nucleuspowered.nucleus.modules.fly.FlyPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@EssentialsEquivalent("fly")
@Command(
        aliases = "fly",
        basePermission = FlyPermissions.BASE_FLY,
        commandDescriptionKey = "fly",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = FlyPermissions.EXEMPT_COOLDOWN_FLY),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = FlyPermissions.EXEMPT_WARMUP_FLY),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = FlyPermissions.EXEMPT_COST_FLY)
        },
        associatedPermissions = FlyPermissions.OTHERS_FLY
)
public class FlyCommand implements ICommandExecutor { // extends AbstractCommand.SimpleTargetOtherPlayer {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier().createOnlyOtherPlayerPermissionElement(FlyPermissions.OTHERS_FLY),
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer player = context.getPlayerFromArgs();
        final boolean fly = context.getOne(NucleusParameters.OPTIONAL_ONE_TRUE_FALSE).orElse(!player.get(Keys.CAN_FLY).orElse(false));

        if (!this.setFlying(player, fly)) {
            return context.errorResult("command.fly.error");
        }

        context.getServiceCollection().storageManager()
                .getOrCreateUser(player.uniqueId()).thenAccept(x -> x.set(FlyKeys.FLY_TOGGLE, fly));
        if (!context.is(player)) {
            context.sendMessage(fly ? "command.fly.player.on" : "command.fly.player.off", player.name());
        }

        context.sendMessageTo(player, fly ? "command.fly.on" : "command.fly.off");
        return context.successResult();
    }

    private boolean setFlying(final Player pl, final boolean fly) {
        // Only if we don't want to fly, offer IS_FLYING as false.
        return !(!fly && !pl.offer(Keys.IS_FLYING, false).isSuccessful()) && pl.offer(Keys.CAN_FLY, fly).isSuccessful();
    }

}
