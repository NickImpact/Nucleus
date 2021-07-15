/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Parses an argument and tries to get a timespan. Returns in seconds.
 *
 * This parser was taken from
 * https://github.com/dualspiral/Hammer/blob/master/HammerCore/src/main/java/uk/co/drnaylor/minecraft/hammer/core/commands/parsers/TimespanParser.java
 */
public final class TimespanParameter implements ValueParameter<Long> {
    private final Pattern minorTimeString = Pattern.compile("^\\d+$");
    private final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");

    private final int secondsInMinute = 60;
    private final int secondsInHour = 60 * this.secondsInMinute;
    private final int secondsInDay = 24 * this.secondsInHour;
    private final int secondsInWeek = 7 * this.secondsInDay;
    private final IMessageProviderService messageProvider;

    public TimespanParameter(final INucleusServiceCollection serviceCollection) {
        this.messageProvider = serviceCollection.messageProvider();
    }

    private long amount(@Nullable final String g, final int multipler) {
        if (g != null && g.length() > 0) {
            return multipler * Long.parseUnsignedLong(g);
        }

        return 0;
    }

    @Override
    public List<CommandCompletion> complete(final CommandContext contex, final String string) {
        return Collections.emptyList();
    }

    @Override
    public Optional<? extends Long> parseValue(final Parameter.Key<? super Long> parameterKey, final ArgumentReader.Mutable reader,
            final CommandContext.Builder context)
            throws ArgumentParseException {
        final String s = reader.parseString();

        // First, if just digits, return the number in seconds.
        if (this.minorTimeString.matcher(s).matches()) {
            return Optional.of(Long.parseUnsignedLong(s));
        }

        final Matcher m = this.timeString.matcher(s);
        if (m.matches()) {
            long time = this.amount(m.group(2), this.secondsInWeek);
            time += this.amount(m.group(4), this.secondsInDay);
            time += this.amount(m.group(6), this.secondsInHour);
            time += this.amount(m.group(8), this.secondsInMinute);
            time += this.amount(m.group(10), 1);

            if (time > 0) {
                return Optional.of(time);
            }
        }

        throw reader.createException(this.messageProvider.getMessageFor(context.cause().audience(), "args.timespan.incorrectformat", s));
    }
}
