/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.commandelement;

import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class LocaleElement implements ValueParameter<Locale> {

    private final INucleusServiceCollection serviceCollection;

    public LocaleElement(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
    }

    @Override
    public List<CommandCompletion> complete(final CommandContext context, final String string) {
        return this.serviceCollection.messageProvider().getAllLocaleNames().stream().filter(x -> x.startsWith(string))
                .map(CommandCompletion::of)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends Locale> parseValue(final Parameter.Key<? super Locale> parameterKey, final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final String s = reader.parseString();
        return Optional.of(this.serviceCollection.messageProvider().getLocaleFromName(s).orElseGet(() -> Locale.forLanguageTag(s.replace("_", "-"))));
    }
}
