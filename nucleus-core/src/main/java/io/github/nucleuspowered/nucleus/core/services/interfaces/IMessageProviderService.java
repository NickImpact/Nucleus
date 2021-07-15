/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.core.services.impl.messageprovider.MessageProviderService;
import io.github.nucleuspowered.nucleus.core.services.impl.messageprovider.repository.ConfigFileMessagesRepository;
import io.github.nucleuspowered.nucleus.core.services.impl.messageprovider.repository.IMessageRepository;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@ImplementedBy(MessageProviderService.class)
public interface IMessageProviderService {

    boolean hasKey(String key);

    Locale getDefaultLocale();

    void invalidateLocaleCacheFor(UUID uuid);

    Optional<Locale> getLocaleFromName(String name);

    Locale getLocaleFor(Audience commandSource);

    Component getMessageFor(Locale locale, String key);

    Component getMessageFor(Locale locale, String key, Component... args);

    Component getMessageFor(Locale locale, String key, Object... replacements);

    Component getMessageFor(Locale locale, String key, String... replacements);

    String getMessageString(Locale locale, String key, String... replacements);

    String getMessageString(Locale locale, String key, Object... replacements);

    default Component getMessageForDefault(final String key, final Component... args) {
        return this.getMessageFor(this.getDefaultLocale(), key, (Object[]) args);
    }

    default Component getMessageFor(final Audience source, final String key) {
        return this.getMessageFor(this.getLocaleFor(source), key);
    }

    default Component getMessageFor(final Audience source, final String key, final Component... args) {
        return this.getMessageFor(this.getLocaleFor(source), key, (Object[]) args);
    }

    default Component getMessageFor(final Audience source, final String key, final String... args) {
        final Component[] t = Arrays.stream(args).map(LegacyComponentSerializer.legacyAmpersand()::deserialize).toArray(Component[]::new);
        return this.getMessageFor(this.getLocaleFor(source), key, (Object[]) t);
    }

    default Component getMessage(final String key) {
        return this.getMessageForDefault(key);
    }

    default Component getMessage(final String key, final String... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, replacements);
    }

    default Component getMessage(final String key, final Component... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, (Object[]) replacements);
    }

    default Component getMessage(final String key, final Object... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, replacements);
    }

    default String getMessageString(final String key, final Object... replacements) {
        return this.getMessageString(this.getDefaultLocale(), key, replacements);
    }

    default String getMessageString(final Audience source, final String key, final String... replacements) {
        return this.getMessageString(this.getLocaleFor(source), key, replacements);
    }

    default Component getMessageFor(final Audience source, final String key, final Object... replacements) {
        return this.getMessageFor(this.getLocaleFor(source), key, replacements);
    }

    default void sendMessageTo(final Audience receiver, final String key) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key));
    }

    default void sendMessageTo(final Audience receiver, final String key, final Object... replacements) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key, replacements));
    }

    default void sendMessageTo(final Audience receiver, final String key, final String... replacements) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key, replacements));
    }

    default void sendMessageTo(final Supplier<Audience> receiver, final String key, final String... replacements) {
        this.sendMessageTo(receiver.get(), key, replacements);
    }

    default void sendMessageTo(final Supplier<Audience> receiver, final String key, final Object... replacements) {
        this.sendMessageTo(receiver.get(), key, replacements);
    }

    boolean reloadMessageFile();

    IMessageRepository getMessagesRepository(Locale locale);

    ConfigFileMessagesRepository getConfigFileMessageRepository();

    default String getTimeToNow(final Audience audience, final Instant instant) {
        return this.getTimeToNow(this.getLocaleFor(audience), instant);
    }

    default String getTimeToNow(final Locale locale, final Instant instant) {
        return this.getTimeString(locale, Instant.now().getEpochSecond() - instant.getEpochSecond());
    }

    default String getTimeString(final Audience locale, final Duration duration) {
        return this.getTimeString(this.getLocaleFor(locale), duration);
    }

    default String getTimeString(final Audience locale, final long duration) {
        return this.getTimeString(this.getLocaleFor(locale), duration);
    }

    String getTimeString(Locale locale, Duration duration);

    String getTimeString(Locale locale, long time);

    Collection<String> getAllLocaleNames();
}
