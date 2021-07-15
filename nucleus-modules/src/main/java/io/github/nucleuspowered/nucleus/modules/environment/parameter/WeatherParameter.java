/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.parameter;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.world.weather.WeatherType;
import org.spongepowered.api.world.weather.WeatherTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WeatherParameter implements ValueParameter<WeatherType> {

    private static final Map<String, WeatherType> WEATHERS = new HashMap<>();

    static {
        WEATHERS.put("clear", WeatherTypes.CLEAR.get());
        WEATHERS.put("c", WeatherTypes.CLEAR.get());
        WEATHERS.put("sun", WeatherTypes.CLEAR.get());
        WEATHERS.put("rain", WeatherTypes.RAIN.get());
        WEATHERS.put("r", WeatherTypes.RAIN.get());
        WEATHERS.put("storm", WeatherTypes.THUNDER.get());
        WEATHERS.put("thunder", WeatherTypes.THUNDER.get());
        WEATHERS.put("t", WeatherTypes.THUNDER.get());
    }

    private final IMessageProviderService messageProvider;

    public WeatherParameter(final IMessageProviderService messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public List<CommandCompletion> complete(final CommandContext context, final String currentInput) {
        return WeatherParameter.WEATHERS.keySet().stream()
                .filter(weather -> weather.toLowerCase().startsWith(currentInput))
                .map(CommandCompletion::of)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends WeatherType> parseValue(
            final Parameter.Key<? super WeatherType> parameterKey,
            final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final String arg = reader.parseString().toLowerCase();
        if (WeatherParameter.WEATHERS.containsKey(arg)) {
            return Optional.of(WeatherParameter.WEATHERS.get(arg));
        }

        throw reader.createException(this.messageProvider.getMessageFor(context.cause().audience(), "args.weather.noexist", "clear, rain, storm"));
    }
}
