/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.parameter;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.util.MinecraftDayTime;
import org.spongepowered.api.util.Ticks;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses an argument for world time. Could be either:
 *
 * <ul>
 *     <li>[0-23]h, for 24 hour time.</li>
 *     <li>[1-12][am|pm], for 12 hour time.</li>
 *     <li>[0-23999], for ticks.</li>
 * </ul>
 *
 * It could also be one of the pre-defined keywords:
 *
 * <ul>
 *     <li>dawn, sunrise: 0 ticks (6 am)</li>
 *     <li>day, daytime, morning: 1000 ticks (7 am)</li>
 *     <li>noon, afternoon: 6000 ticks (12 pm)</li>
 *     <li>dusk, evening, sunset: 12000 ticks (6 pm)</li>
 *     <li>night: 14000 ticks (8 pm)</li>
 *     <li>midnight: 18000 ticks (12 am)</li>
 * </ul>
 */
public class WorldTimeParameter implements ValueParameter<MinecraftDayTime> {

    private static final HashMap<String, MinecraftDayTime> TICK_ALIASES = new HashMap<>();
    private static final Pattern tfh = Pattern.compile("^(\\d{1,2})[hH]$");
    private static final Pattern ampm = Pattern.compile("^(\\d{1,2})(a[m]?|p[m]?)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern ticks = Pattern.compile("^(\\d+)$");

    private static final MinecraftDayTime DAWN = MinecraftDayTime.minecraftEpoch();
    private static final MinecraftDayTime DAY = MinecraftDayTime.of(1, 7, 0);
    private static final MinecraftDayTime NOON = MinecraftDayTime.of(1, 12, 0);
    private static final MinecraftDayTime DUSK = MinecraftDayTime.of(1, 18, 0);
    private static final MinecraftDayTime NIGHT = MinecraftDayTime.of(1, 20, 0);
    private static final MinecraftDayTime MIDNIGHT = MinecraftDayTime.of(2, 0, 0);

    private final boolean allowAliases;

    // Thanks to http://minecraft.gamepedia.com/Day-night_cycle
    static {
        TICK_ALIASES.put("dawn", DAWN);
        TICK_ALIASES.put("sunrise", DAWN);
        TICK_ALIASES.put("morning", DAY);
        TICK_ALIASES.put("day", DAY);
        TICK_ALIASES.put("daytime", DAY);
        TICK_ALIASES.put("noon", NOON);
        TICK_ALIASES.put("afternoon", NOON);
        TICK_ALIASES.put("dusk", DUSK);
        TICK_ALIASES.put("sunset", DUSK);
        TICK_ALIASES.put("evening", DUSK);
        TICK_ALIASES.put("night", NIGHT);
        TICK_ALIASES.put("midnight", MIDNIGHT);
    }

    private final IMessageProviderService messageProvider;

    public WorldTimeParameter(final boolean allowAliases, final IMessageProviderService messageProviderService) {
        this.allowAliases = allowAliases;
        this.messageProvider = messageProviderService;
    }

    private MinecraftDayTime getValue(final CommandContext source, final ArgumentReader.Mutable reader, final String arg) throws ArgumentParseException {
        if (this.allowAliases && TICK_ALIASES.containsKey(arg)) {
            return WorldTimeParameter.TICK_ALIASES.get(arg);
        }
        final Audience audience = source.cause().audience();

        // <number>h
        final Matcher m1 = tfh.matcher(arg);
        if (m1.matches()) {
            // Get the number, multiply by 1000, return.
            final long i = Long.parseLong(m1.group(1));
            if (i > 23 || i < 0) {
                throw reader.createException(this.messageProvider.getMessageFor(audience, "args.worldtime.24herror"));
            }

            return MinecraftDayTime.of(i < 6 ? 1 : 0, (int) i, 0);
        }

        // <number>am,pm
        final Matcher m2 = ampm.matcher(arg);
        if (m2.matches()) {
            // Get the number, multiply by 1000, return.
            int i = Integer.parseInt(m2.group(1));
            if (i > 12 || i < 1) {
                throw reader.createException(this.messageProvider.getMessageFor(audience, "args.worldtime.12herror"));
            }

            // Modify to 24 hour time, based on am/pm
            final String id = m2.group(2).toLowerCase();
            if (id.startsWith("p") && i < 12) {
                // 11 pm -> 23, 12 pm -> 12.
                i += 12;
            } else if (id.startsWith("a") && i == 12) {
                // 12 am -> 0
                i = 0;
            }

            return MinecraftDayTime.of(i < 6 ? 1 : 0, i, 0);
        }

        // 0 -> 23999
        if (ticks.matcher(arg).matches()) {
            return MinecraftDayTime.of(Sponge.server(), Ticks.of(Long.parseLong(arg)));
        }

        throw reader.createException(this.messageProvider.getMessageFor(audience, "args.worldtime.error", arg));
    }

    @Override
    public List<CommandCompletion> complete(final CommandContext context, final String currentInput) {
        return WorldTimeParameter.TICK_ALIASES.keySet().stream().filter(x -> x.startsWith(currentInput))
                .map(CommandCompletion::of)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends MinecraftDayTime> parseValue(
            final Parameter.Key<? super MinecraftDayTime> parameterKey,
            final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final String arg = reader.parseString();
        return Optional.of(this.getValue(context, reader, arg));
    }

}
