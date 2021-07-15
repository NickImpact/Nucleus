/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.core.util.TypeTokens;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.modules.world.config.WorldConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.WorldType;
import org.spongepowered.api.world.WorldTypes;
import org.spongepowered.api.world.border.WorldBorder;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.generation.config.WorldGenerationConfig;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldManager;
import org.spongepowered.api.world.server.WorldTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Command(
        aliases = {"create"},
        basePermission = WorldPermissions.BASE_WORLD_CREATE,
        commandDescriptionKey = "world.create",
        parentCommand = WorldCommand.class
)
public class CreateWorldCommand implements ICommandExecutor, IReloadableService.Reloadable {

    @Nullable private Long worldBorderDefault;

    private final Parameter.Value<WorldType> worldType = Parameter.registryElement(TypeTokens.WORLD_TYPE, RegistryTypes.WORLD_TYPE)
            .key("world type")
            .build();
    private final Parameter.Value<Difficulty> difficultyValue =
            Parameter.registryElement(TypeTokens.DIFFICULTY, RegistryTypes.DIFFICULTY).key("difficulty").build();
    private final Parameter.Value<GameMode> gameMode = Parameter.registryElement(TypeTokens.GAME_MODE, RegistryTypes.GAME_MODE).key("gamemode").build();
    private final Parameter.Value<Long> seedValue = Parameter.longNumber().key("seed").build();
    private final Parameter.Value<Boolean> loadOnStartup = Parameter.bool().key("loadOnStartup").build();
    private final Parameter.Value<Boolean> keepSpawnLoaded = Parameter.bool().key("keepSpawnLoaded").build();
    private final Parameter.Value<Boolean> allowCommands = Parameter.bool().key("allowCommands").build();
    private final Parameter.Value<Boolean> structures = Parameter.bool().key("structures").build();
    private final Parameter.Value<Boolean> bonusChest = Parameter.bool().key("bonusChest").build();
    private final Parameter.Value<String> nameValue = Parameter.string().key("name").build();
    private final Parameter.Value<Boolean> pvpEnabled = Parameter.bool().key("pvp").build();

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of(this.allowCommands, "c", "allowcommands"),
                Flag.of(this.bonusChest, "b", "bonuschest"),
                Flag.of(this.worldType, "w", "worldtype"),
                Flag.of(this.difficultyValue, "d", "difficulty"),
                Flag.of(this.gameMode, "gm", "gamemode"),
                Flag.of(this.seedValue, "s", "seed"),
                Flag.of(this.loadOnStartup, "l", "loadonstartup"),
                Flag.of(this.structures, "st", "structures"),
                Flag.of(this.keepSpawnLoaded, "k", "keepspawnloaded"),
                Flag.of(this.pvpEnabled, "pvp")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.nameValue
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String nameInput = context.requireOne(this.nameValue);
        final Optional<WorldType> dimensionInput = context.getOne(this.worldType);
        final Optional<GameMode> gamemodeInput = context.getOne(this.gameMode);
        final Optional<Difficulty> difficultyInput = context.getOne(this.difficultyValue);
        final Optional<Long> seedInput = context.getOne(this.seedValue);
        final boolean genStructures = context.getOne(this.structures).orElse(true);
        final boolean loadOnStartup = context.getOne(this.loadOnStartup).orElse(true);
        final boolean keepSpawnLoaded = context.getOne(this.keepSpawnLoaded).orElse(true);
        final boolean allowCommands = context.getOne(this.allowCommands).orElse(true);
        final boolean bonusChest = context.getOne(this.bonusChest).orElse(true);
        final boolean pvp = context.getOne(this.pvpEnabled).orElse(true);

        final ResourceKey projectedKey = ResourceKey.of(context.getServiceCollection().pluginContainer(), nameInput.toLowerCase());
        final WorldManager worldManager = Sponge.server().worldManager();
        if (worldManager.templateExists(projectedKey)) {
            return context.errorResult("command.world.create.exists", nameInput);
        }

        final DefaultedRegistryReference<WorldType> type = dimensionInput.map(x -> x.asDefaultedReference(RegistryTypes.WORLD_TYPE))
                .orElse(WorldTypes.OVERWORLD);

        final WorldTemplate.Builder template = WorldTemplate.builder().worldType(type);

        template.key(projectedKey);
        gamemodeInput.ifPresent(x -> template.gameMode(x.asDefaultedReference(RegistryTypes.GAME_MODE)));
        difficultyInput.ifPresent(x -> template.difficulty(x.asDefaultedReference(RegistryTypes.DIFFICULTY)));

        final WorldGenerationConfig.Mutable.Builder genConfig = WorldGenerationConfig.Mutable.builder()
                .generateBonusChest(bonusChest)
                .generateFeatures(genStructures);

        seedInput.ifPresent(genConfig::seed);

        template.loadOnStartup(loadOnStartup)
                .generationConfig(genConfig.build())
                .commands(allowCommands)
                .pvp(pvp);

        final WorldTemplate completedTemplate = template.build();

        context.sendMessage("command.world.create.begin", nameInput);
        context.sendMessage("command.world.create.newparams",
                type.location().asString(),
                "-",
                completedTemplate.gameMode().orElse(GameModes.NOT_SET).location().asString(),
                completedTemplate.difficulty().map(x -> x.location().asString()).orElseGet(() -> context.getMessageString("standard.notset")));
        context.sendMessage("command.world.create.newparams2",
                String.valueOf(loadOnStartup),
                String.valueOf(keepSpawnLoaded),
                String.valueOf(genStructures),
                String.valueOf(allowCommands),
                String.valueOf(bonusChest));

        worldManager.saveTemplate(template.build()).handle((created, exception) -> {
            if (exception != null) {
                context.getServiceCollection().schedulerService().runOnMainThread(
                        () -> context.sendMessageText(Component.text(exception.getMessage())));
                final CompletableFuture<ServerWorld> worldCompletableFuture = new CompletableFuture<>();
                worldCompletableFuture.complete(null);
                return worldCompletableFuture;
            } else if (!created) {
                context.getServiceCollection().schedulerService().runOnMainThread(
                        () -> context.sendMessage("command.world.create.worldfailedtoload", nameInput));
                final CompletableFuture<ServerWorld> worldCompletableFuture = new CompletableFuture<>();
                worldCompletableFuture.complete(null);
                return worldCompletableFuture;
            }

            return Sponge.server().worldManager().loadWorld(projectedKey).<Void>handle((world, innerException) -> {
                if (innerException != null) {
                    context.getServiceCollection().schedulerService().runOnMainThread(
                            () -> context.sendMessage("command.world.create.worldfailedtoload", nameInput));
                    return null;
                }

                if (world != null) {
                    if (this.worldBorderDefault != null && this.worldBorderDefault > 0) {
                        final WorldBorder.Builder builder = world.border().toBuilder();
                        builder.initialDiameter(this.worldBorderDefault).targetDiameter(this.worldBorderDefault);
                        world.setBorder(builder.build());
                    }
                    context.getServiceCollection().schedulerService().runOnMainThread(
                            () -> context.sendMessage("command.world.create.success", nameInput));
                }
                return null;
            });
        });
        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.worldBorderDefault = serviceCollection
                .configProvider()
                .getModuleConfig(WorldConfig.class)
                .getWorldBorderDefault()
                .orElse(null);
    }

}
