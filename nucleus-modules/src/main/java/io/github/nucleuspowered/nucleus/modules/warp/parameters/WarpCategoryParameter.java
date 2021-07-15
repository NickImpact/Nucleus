/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.parameters;

import io.github.nucleuspowered.nucleus.api.module.warp.data.WarpCategory;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class WarpCategoryParameter implements ValueParameter<WarpCategory> {

    private final WarpService handler;
    private final IMessageProviderService messageProvider;

    public WarpCategoryParameter(final INucleusServiceCollection serviceCollection, final WarpService handler) {
        this.handler = handler;
        this.messageProvider = serviceCollection.messageProvider();
    }

    @Override
    public List<CommandCompletion> complete(final CommandContext context, final String currentInput) {
        return this.handler.getWarpsWithCategories().keySet().stream().filter(Objects::nonNull)
                .filter(x -> x.getId().startsWith(currentInput))
                .map(x -> CommandCompletion.of(x.getId(), x.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends WarpCategory> parseValue(final Parameter.Key<? super WarpCategory> parameterKey, final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final String arg = reader.parseString();
        final Optional<WarpCategory> category =
                this.handler.getWarpsWithCategories().keySet().stream().filter(Objects::nonNull).filter(x -> x.getId().equals(arg)).findFirst();
        if (category.isPresent()) {
            return category;
        }

        throw reader.createException(this.messageProvider.getMessageFor(context.cause().audience(), "args.warpcategory.noexist", arg));
    }
}
