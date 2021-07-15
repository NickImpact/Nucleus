/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fun.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.fun.FunPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.core.util.functional.NucleusCollectors;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.standard.ResourceKeyedValueParameters;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.Collection;
import java.util.Optional;

@EssentialsEquivalent(value = {"lightning", "strike", "smite", "thor", "shock"}, isExact = false,
        notes = "Selectors can be used, entities can be struck.")
@Command(
        aliases = {"lightning", "strike", "smite", "thor", "shock"},
        basePermission = FunPermissions.BASE_LIGHTNING,
        commandDescriptionKey = "lightning",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = FunPermissions.EXEMPT_COOLDOWN_LIGHTNING),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = FunPermissions.EXEMPT_WARMUP_LIGHTNING),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = FunPermissions.EXEMPT_COST_LIGHTNING)
        },
        associatedPermissions = FunPermissions.OTHERS_LIGHTNING
)
public class LightningCommand implements ICommandExecutor {

    private final Parameter.Value<Collection<Entity>> manyLivingParameter;

    @Inject
    public LightningCommand(final IPermissionService permissionService) {
        this.manyLivingParameter = Parameter.builder(new TypeToken<Collection<Entity>>() {})
            .optional()
            .requirements(cause -> permissionService.hasPermission(cause, FunPermissions.OTHERS_LIGHTNING))
            .key("targets")
            .addParser(ResourceKeyedValueParameters.MANY_ENTITIES)
            .build();
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.manyLivingParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Collection<Living> playerCollection =
                context.requireOne(this.manyLivingParameter)
                        .stream()
                        .collect(NucleusCollectors.toFilteredElementCollection(Living.class));

        // No argument, let's not smite the subject.
        if (playerCollection.isEmpty()) {
            final Player pl = context.getIfPlayer();

            // 100 is a good limit here.
            final Optional<RayTraceResult<LocatableBlock>> result =
                    RayTrace.block().sourceEyePosition(pl).limit(100).select(RayTrace.nonAir()).continueWhileBlock(RayTrace.onlyAir()).execute();
            // Smite above, but not on.
            final ServerLocation lightningLocation =
                    result.map(RayTraceResult::hitPosition)
                            .map(x -> ServerLocation.of(pl.serverLocation().world(), x.x(), x.y(), x.z()))
                            .orElseGet(() -> pl.serverLocation().add(0.0, 3.0, 0.0));

            this.spawnLightning(lightningLocation, context, null);
            return context.successResult();
        }

        for (final Living pl : playerCollection) {
            this.spawnLightning(
                    pl.serverLocation(),
                    context,
                    pl instanceof Player ? (Player)pl : null);
        }

        return context.successResult();
    }

    private void spawnLightning(
            final ServerLocation location,
            final ICommandContext context,
            @Nullable final Player target) throws CommandException {

        final ServerWorld world = location.world();
        final Entity bolt = world.createEntity(EntityTypes.LIGHTNING_BOLT.get(), location.position());

        try (final CauseStackManager.StackFrame frame = Sponge.server().causeStackManager().pushCauseFrame()) {
            world.spawnEntity(bolt);
            if (target != null) {
                context.sendMessage("command.lightning.success.other",
                        context.getServiceCollection().playerDisplayNameService().getDisplayName(target.uniqueId()));
            }
        }

        if (target != null) {
            throw context.createException("command.lightning.errorplayer", context.getServiceCollection().playerDisplayNameService().getDisplayName(target.uniqueId()));
        } else {
            throw context.createException("command.lightning.error");
        }

    }
}
