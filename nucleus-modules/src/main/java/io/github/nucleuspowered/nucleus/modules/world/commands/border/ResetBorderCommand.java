/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.border;

import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.border.WorldBorder;
import org.spongepowered.api.world.server.ServerWorld;

@Command(
        aliases = { "reset" },
        basePermission = WorldPermissions.BASE_BORDER_SET,
        commandDescriptionKey = "world.border.reset",
        parentCommand = BorderCommand.class
)
public class ResetBorderCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONLINE_WORLD_OPTIONAL
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerWorld world = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.ONLINE_WORLD_OPTIONAL)
                .orElseThrow(() -> context.createException("command.world.player"));

        final WorldBorder worldBorder = WorldBorder.builder().overworldDefaults().build();
        world.setBorder(WorldBorder.builder().overworldDefaults().build());
        context.sendMessage("command.world.setborder.set",
                world.key().asString(),
                "0",
                "0",
                String.valueOf(worldBorder.diameter()));

        return context.successResult();
    }
}
