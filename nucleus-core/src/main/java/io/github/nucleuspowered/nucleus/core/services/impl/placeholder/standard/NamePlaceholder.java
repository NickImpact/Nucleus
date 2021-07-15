/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.placeholder.standard;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerDisplayNameService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.placeholder.PlaceholderParser;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public class NamePlaceholder<T> implements PlaceholderParser {

    private static final Component CONSOLE = Component.text("-");
    private final IPlayerDisplayNameService playerDisplayNameService;
    private final boolean consoleFilter;
    private final BiFunction<IPlayerDisplayNameService, T, Component> parser;
    private final ResourceKey resourceKey;
    private final Class<T> clazz;

    public NamePlaceholder(
            final Class<T> clazz,
            final IPlayerDisplayNameService playerDisplayNameService,
            final BiFunction<IPlayerDisplayNameService, T, Component> parser,
            final String id) {
        this(clazz, playerDisplayNameService, parser, id, false);
    }

    public NamePlaceholder(
            final Class<T> clazz,
            final IPlayerDisplayNameService playerDisplayNameService,
            final BiFunction<IPlayerDisplayNameService, T, Component> parser,
            final String id,
            final boolean consoleFilter) {
        this.clazz = clazz;
        this.playerDisplayNameService = playerDisplayNameService;
        this.parser = parser;
        this.consoleFilter = consoleFilter;
        this.resourceKey = ResourceKey.resolve(id);
    }

    @Override
    public Component parse(final PlaceholderContext placeholder) {
        final Optional<Object> associated = placeholder.associatedObject();
        if (associated.isPresent()) {
            if (this.consoleFilter && associated.get() instanceof SystemSubject || associated.get() instanceof Server) {
                return CONSOLE;
            } else if (this.clazz.isInstance(associated.get())) {
                return this.parser.apply(this.playerDisplayNameService, this.clazz.cast(associated.get()));
            }
        }
        return Component.empty();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final NamePlaceholder<?> that = (NamePlaceholder<?>) o;
        return this.consoleFilter == that.consoleFilter && Objects.equals(this.playerDisplayNameService, that.playerDisplayNameService)
                && Objects.equals(this.parser, that.parser) && Objects.equals(this.resourceKey, that.resourceKey) && Objects
                .equals(this.clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.playerDisplayNameService, this.consoleFilter, this.parser, this.resourceKey, this.clazz);
    }
}
