/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.command.parameter;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.command.parameter.managed.standard.ResourceKeyedValueParameters;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class AudienceValueParameter implements ValueParameter<List<Audience>> {

    private static final CommandCompletion CONSOLE = CommandCompletion.of("-", Component.text("Console"));

    @Override
    public List<CommandCompletion> complete(final CommandContext context, final String currentInput) {
        if (currentInput.equals("-")) {
            return Collections.singletonList(AudienceValueParameter.CONSOLE);
        }
        return ResourceKeyedValueParameters.MANY_PLAYERS.get().complete(context, currentInput);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Optional<? extends List<Audience>> parseValue(
            final Parameter.Key<? super List<Audience>> parameterKey,
            final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final String r = reader.peekString();
        if (r.equals("-")) {
            reader.parseString();
            return Optional.of(Collections.singletonList(Sponge.systemSubject()));
        }
        return ResourceKeyedValueParameters.MANY_PLAYERS.get().parseValue((Parameter.Key) parameterKey, reader, context);
    }
}
