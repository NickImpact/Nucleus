/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;
import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Command(
        aliases = {"list", "ls", "#worlds"},
        basePermission = WorldPermissions.BASE_WORLD_INFO,
        commandDescriptionKey = "world.list",
        parentCommand = WorldCommand.class,
        associatedPermissions = {
                WorldPermissions.WORLD_SEED
        }
)
public class ListWorldCommand implements ICommandExecutor {

    static void getWorldInfo(
            final ICommandContext context, final List<Component> listContent, final ServerWorld world, final boolean canSeeSeeds) {
        // Name of world
        if (!listContent.isEmpty()) {
            listContent.add(Component.space());
        }

        final ServerWorldProperties properties = world.properties();
        listContent.add(context.getMessage("command.world.list.worlditem", world.key().asString()));

        // As requested by Pixelmon for use in their config.
        // x.getAdditionalProperties().getInt(DataQuery.of("SpongeData", "dimensionId")).ifPresent(i ->
        //    listContent.add(context.getMessage("command.world.list.dimensionid", String.valueOf(i))));
        final Vector3i spawnPosition = properties.spawnPosition();
        listContent.add(context.getMessage("command.world.list.spawnpoint",
                String.valueOf(spawnPosition.x()), String.valueOf(spawnPosition.y()), String.valueOf(spawnPosition.z())));

        listContent.add(context.getMessage("command.world.list.uuid", world.uniqueId().toString()));

        if (canSeeSeeds) {
            listContent.add(context.getMessage("command.world.list.seed", String.valueOf(world.seed())));
        }

        listContent.add(context.getMessage("command.world.list.params",
            world.worldType().findKey(RegistryTypes.WORLD_TYPE).map(ResourceKey::asString).orElse("custom"),
            "-",
            properties.gameMode().asComponent(),
            properties.difficulty().asComponent()));

        listContent.add(context.getMessage("command.world.list.params2",
            String.valueOf(properties.hardcore()),
            String.valueOf(properties.loadOnStartup()),
            String.valueOf(properties.pvp()),
            "-"
        ));
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get all the worlds
        final Collection<ServerWorld> cwp = Sponge.server().worldManager().worlds();
        final List<Component> listContent = new ArrayList<>();

        final boolean canSeeSeeds = context.testPermission(WorldPermissions.WORLD_SEED);
        cwp.stream().sorted(Comparator.comparing(x -> x.key().asString()))
                .forEach(x -> getWorldInfo(context, listContent, x, canSeeSeeds));

        Util.getPaginationBuilder(context.audience())
            .contents(listContent).title(context.getMessage("command.world.list.title"))
            .sendTo(context.audience());

        return context.successResult();
    }
}
