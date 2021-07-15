/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.info.parameter;

import io.github.nucleuspowered.nucleus.core.io.TextFileController;
import io.github.nucleuspowered.nucleus.modules.info.services.InfoHandler;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class InfoValueParameter implements ValueParameter<InfoValueParameter.Result> {

    private final InfoHandler handler;
    private final IMessageProviderService messageProviderService;

    public InfoValueParameter(final InfoHandler handler, final INucleusServiceCollection serviceCollection) {
        this.handler = Objects.requireNonNull(handler);
        this.messageProviderService = serviceCollection.messageProvider();
    }

    @Override
    public List<CommandCompletion> complete(final CommandContext context, final String currentInput) {
        try {
            return this.handler.getInfoSections().stream()
                    .filter(x -> x.toLowerCase().startsWith(currentInput.toLowerCase()))
                    .map(CommandCompletion::of)
                    .collect(Collectors.toList());
        } catch (final Exception e) {
            return this.handler.getInfoSections().stream().map(CommandCompletion::of).collect(Collectors.toList());
        }
    }

    @Override
    public Optional<? extends Result> parseValue(final Parameter.Key<? super Result> parameterKey, final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {

        final String a = reader.parseString();
        final Optional<TextFileController> list = this.handler.getSection(a);
        if (list.isPresent()) {
            return Optional.of(new Result(this.handler.getInfoSections().stream().filter(a::equalsIgnoreCase).findFirst().get(), list.get()));
        }

        throw reader.createException(this.messageProviderService.getMessageFor(context.cause().audience(), "args.info.noinfo", a));
    }

    public static class Result {
        public final String name;
        public final TextFileController text;

        public Result(final String name, final TextFileController text) {
            this.name = name;
            this.text = text;
        }
    }
}
