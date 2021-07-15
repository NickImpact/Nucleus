/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.text;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.placeholder.NucleusPlaceholderService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a template that Nucleus uses to create texts from templates.
 */
public interface NucleusTextTemplate extends ComponentLike {

    /**
     * Creates a {@link NucleusTextTemplate}.
     *
     * @see NucleusTextTemplateFactory#createFromAmpersandString(String)
     *
     * @param ampersandString The ampersand encoded string
     * @return The {@link NucleusTextTemplate}
     */
    static NucleusTextTemplate create(final String ampersandString) {
        return NucleusAPI.getTextTemplateFactory().createFromAmpersandString(ampersandString);
    }

    /**
     * Whether the text is empty.
     *
     * @return <code>true</code> if so.
     */
    boolean isEmpty();

    /**
     * Gets the static {@link Component} this message will be prefixed with, if any.
     *
     * @return The text
     */
    Optional<Component> getPrefix();

    /**
     * Gets the {@link Component} where the tokens have been parsed from the
     * viewpoint of the supplied {@link Object}. Any unknown tokens in
     * the parsed text will be left blank.
     *
     * <p>Unlike {@link #getForObject(Object)}, this will
     * <strong>not</strong> contain the prefix and suffix in the message.</p>
     *
     * @param source The {@link Object} that will influence what is
     *               displayed by the tokens.
     * @return The parsed {@link Component}
     */
    Component getBody(Object source);

    /**
     * Gets the {@link Component} where the tokens have been parsed from the
     * viewpoint of the supplied {@link Object}. Any unknown tokens in
     * the parsed text will be left blank.
     *
     * <p>Unlike {@link #getForObjectWithSenderToken(Object, Object)},
     * this will <strong>not</strong> contain the prefix and suffix in the
     * message.</p>
     *
     * @param source The {@link Object} that will influence what is displayed by the tokens.
     * @param sender The {@link Object} that can be considered the <code>{{sender}}</code>
     * @return The parsed {@link Component}
     */
    Component getBody(Object source, Object sender);

    /**
     * Gets the {@link Component} where the tokens have been parsed from the
     * viewpoint of the supplied {@link Object}.
     *
     * <p>
     *     By supplying a token array, these token identifiers act as additional
     *     tokens that could be encountered, and will be used above standard
     *     tokens. This is useful for having a token in a specific context, such
     *     as "displayfrom", which might only be used in a message, and is not
     *     worth registering in a {@link NucleusPlaceholderService}. They must
     *     not contain the token start or end delimiters.
     * </p>
     *
     * <p>Unlike {@link #getForObjectWithTokens(Object, Map)},
     * this will <strong>not</strong> contain the prefix and suffix in the
     * message.</p>
     *
     * @param source The {@link Object} that will influence what is displayed by the tokens.
     * @param tokensArray The extra tokens that can be used to parse a text.
     * @return The parsed {@link Component}
     */
    Component getBody(Object source,
            @Nullable Map<String, Function<Object, Optional<ComponentLike>>> tokensArray);

    /**
     * Gets the static {@link Component} this message will be suffixed with, if any.
     *
     * @return The text
     */
    Optional<Component> getSuffix();

    /**
     * Returns whether there are tokens to parse.
     *
     * @return <code>true</code> if there are tokens.
     */
    boolean containsTokens();

    /**
     * Gets the {@link Component} where the tokens have been parsed from the viewpoint of the
     * supplied {@link Audience}. Any unknown tokens in the parsed text will be left blank.
     *
     * @param source The {@link Object} that will influence what is displayed by the tokens.
     * @return The parsed {@link Component}
     */
    Component getForObject(Object source);

    /**
     * Gets the {@link Component} where the tokens have been parsed from the viewpoint of the
     * supplied {@link Object}. Any unknown tokens in the parsed text will be left blank.
     *
     * @param source The {@link Object} that will influence what is displayed by the tokens.
     * @param sender The {@link Object} that can be considered the <code>{{sender}}</code>
     * @return The parsed {@link Component}
     */
    Component getForObjectWithSenderToken(Object source, Object sender);

    /**
     * Gets the {@link Component} where the tokens have been parsed from the viewpoint of the supplied {@link Object}.
     *
     * <p>
     *     By supplying a token array, these token identifiers act as additional
     *     tokens that could be encountered, and will be used above standard
     *     tokens. This is useful for having a token in a specific context, such
     *     as "displayfrom", which might only be used in a message, and is not
     *     worth registering in a {@link NucleusPlaceholderService}. They must
     *     not contain the token start or end delimiters.
     * </p>
     *
     * @param source The {@link Object} that will influence what is displayed by the tokens.
     * @param tokensArray The extra tokens that can be used to parse a text.
     * @return The parsed {@link Component}
     */
    Component getForObjectWithTokens(Object source, @Nullable Map<String, Function<Object, Optional<ComponentLike>>> tokensArray);

}
