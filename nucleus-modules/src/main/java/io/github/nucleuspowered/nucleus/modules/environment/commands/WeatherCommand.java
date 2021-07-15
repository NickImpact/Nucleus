/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentKeys;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentPermissions;
import io.github.nucleuspowered.nucleus.modules.environment.config.EnvironmentConfig;
import io.github.nucleuspowered.nucleus.modules.environment.parameter.WeatherParameter;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.weather.WeatherType;

import java.time.Duration;
import java.util.Optional;

@EssentialsEquivalent({"thunder", "sun", "weather", "sky", "storm", "rain"})
@Command(
        aliases = {"weather"},
        basePermission = EnvironmentPermissions.BASE_WEATHER,
        commandDescriptionKey = "weather",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = EnvironmentPermissions.EXEMPT_COOLDOWN_WEATHER),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission =  EnvironmentPermissions.EXEMPT_WARMUP_WEATHER),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = EnvironmentPermissions.EXEMPT_COST_WEATHER)
        },
        associatedPermissions = EnvironmentPermissions.WEATHER_EXEMPT_LENGTH
)
public class WeatherCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final Parameter.Value<WeatherType> weatherParameter;

    @Inject
    public WeatherCommand(final IMessageProviderService messageProviderService) {
        this.weatherParameter = Parameter.builder(WeatherType.class)
                .addParser(new WeatherParameter(messageProviderService))
                .key("weather")
                .build();
    }

    private long max = Long.MAX_VALUE;

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.max = serviceCollection.configProvider().getModuleConfig(EnvironmentConfig.class).getMaximumWeatherTimespan();
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONLINE_WORLD_OPTIONAL,
                this.weatherParameter,
                NucleusParameters.OPTIONAL_DURATION
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // We can predict the weather on multiple worlds now!
        final ServerWorld w = context.getWorldPropertiesOrFromSelf(NucleusParameters.ONLINE_WORLD_OPTIONAL);

        // Get whether we locked the weather.
        if (context.getServiceCollection().storageManager().getWorldOnThread(w.key())
                .map(x -> x.get(EnvironmentKeys.LOCKED_WEATHER).orElse(false)).orElse(false)) {
            // Tell the user to unlock first.
            return context.errorResult("command.weather.locked", w.key().asString());
        }

        // Houston, we have a world! Now, what was the forecast?
        final WeatherType we = context.requireOne(this.weatherParameter);

        // Have we gotten an accurate forecast? Do we know how long this weather spell will go on for?
        final Optional<Long> oi = context.getOne(NucleusParameters.OPTIONAL_DURATION).map(Duration::getSeconds);

        // Even weather masters have their limits. Sometimes.
        if (this.max > 0 && oi.orElse(Long.MAX_VALUE) > this.max && !context.testPermission(EnvironmentPermissions.WEATHER_EXEMPT_LENGTH)) {
            return context.errorResult("command.weather.toolong", context.getTimeString(this.max));
        }

        if (oi.isPresent()) {
            // YES! I should get a job at the weather service and show them how it's done!
            Sponge.server().scheduler().submit(Task.builder()
                    .execute(() -> w.setWeather(we, Ticks.ofWallClockSeconds(Sponge.server(), oi.get().intValue())))
                    .plugin(context.getServiceCollection().pluginContainer()).build());
            context.sendMessage("command.weather.time", we.key(RegistryTypes.WEATHER_TYPE).asString(), w.key().asString(),
                    context.getTimeString(oi.get()));
        } else {
            // No, probably because I've already gotten a job at the weather service...
            Sponge.server().scheduler().submit(
                    Task.builder().execute(() -> w.setWeather(we)).plugin(context.getServiceCollection().pluginContainer()).build()
            );
            context.sendMessage("command.weather.set", we.key(RegistryTypes.WEATHER_TYPE).asString(), w.key().asString());
        }

        // The weather control device has been activated!
        return context.successResult();
    }


}
