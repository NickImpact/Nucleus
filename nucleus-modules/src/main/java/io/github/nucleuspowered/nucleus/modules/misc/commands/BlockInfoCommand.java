/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.misc.MiscPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.registry.RegistryType;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.state.StateProperty;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Command(
        aliases = "blockinfo",
        basePermission = MiscPermissions.BASE_BLOCKINFO,
        commandDescriptionKey = "blockinfo",
        associatedPermissions = MiscPermissions.BLOCKINFO_EXTENDED
)
public class BlockInfoCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        final IPermissionService permissionService = serviceCollection.permissionService();
        return new Flag[] {
                Flag.builder().alias("e").alias("extended").setRequirement(commandCause -> permissionService.hasPermission(commandCause,
                        MiscPermissions.BLOCKINFO_EXTENDED)).build()
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            NucleusParameters.OPTIONAL_LOCATION
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<LocatableBlock> loc;
        if (context.hasAny(NucleusParameters.OPTIONAL_LOCATION)) {
            // get the location
            loc = context.getOne(NucleusParameters.OPTIONAL_LOCATION)
                    .filter(x -> !x.blockType().isAnyOf(
                            BlockTypes.CAVE_AIR.get(),
                            BlockTypes.AIR.get(),
                            BlockTypes.VOID_AIR.get()
                    ))
                    .map(ServerLocation::asLocatableBlock);
        } else {
            final ServerPlayer serverPlayer = context.requirePlayer();
            loc = RayTrace.block().sourceEyePosition(serverPlayer).direction(serverPlayer)
                    .select(RayTrace.nonAir())
                    .continueWhileBlock(RayTrace.onlyAir())
                    .execute().map(RayTraceResult::selectedObject);
        }

        if (loc.isPresent()) {
            // get the information.
            final LocatableBlock block = loc.get();
            final BlockState blockState = block.blockState();
            final List<Component> lt = new ArrayList<>();
            lt.add(context.getMessage("command.blockinfo.id",
                    blockState.type().findKey(RegistryTypes.BLOCK_TYPE).map(ResourceKey::formatted)
                            .orElseGet(() -> context.getMessageString("standard.unknown")),
                    blockState.type().asComponent()));
            lt.add(context.getMessage("command.iteminfo.extendedid", blockState.toString()));

            if (context.hasFlag("e")) {
                final Collection<StateProperty<?>> cp = blockState.stateProperties();
                if (!cp.isEmpty()) {
                    cp.forEach(x -> blockState.stateProperty(x).map(String::valueOf)
                        .ifPresent(y -> context.getServiceCollection().messageProvider().getMessageFor(
                                context.audience(),
                                "command.blockinfo.property.item",
                                x.name(),
                                y)));
                }
            }

            final Vector3i pos = block.blockPosition();
            Util.getPaginationBuilder(context.audience()).contents(lt)
                    .padding(Component.text("-", NamedTextColor.GREEN))
                    .title(context.getMessage("command.blockinfo.list.header",
                            pos.x(),
                            pos.y(),
                            pos.z()))
                    .sendTo(context.audience());

            return context.successResult();
        }

        return context.errorResult("command.blockinfo.none");
    }
}
