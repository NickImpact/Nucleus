/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.parameter;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.Monster;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityTypeValueParameter implements ValueParameter<Predicate<Entity>> {

    private static final Predicate<Entity> armourStand = e -> e.type().equals(EntityTypes.ARMOR_STAND.get());
    private static final Predicate<Entity> hostile = e -> e instanceof Monster;
    private static final Predicate<Entity> passive = e -> e instanceof Living && !(e instanceof Player || e instanceof Monster);

    private final Map<String, Predicate<Entity>> map = new HashMap<String, Predicate<Entity>>() {{
        this.put("armorstand", armourStand);
        this.put("armourstand", armourStand);
        this.put("monsters", hostile);
        this.put("hostile", hostile);
        this.put("passive", passive);
        this.put("animal", passive);
        this.put("item", e -> e instanceof Item);
        this.put("player", e -> e instanceof Player);
    }};

    @Override
    public List<CommandCompletion> complete(final CommandContext context, final String input) {
        return this.map.keySet().stream().filter(x -> x.startsWith(input))
                .map(CommandCompletion::of)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends Predicate<Entity>> parseValue(
            final Parameter.Key<? super Predicate<Entity>> parameterKey,
            final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final Predicate<Entity> predicate = this.map.get(reader.toString().toLowerCase());
        if (predicate == null) {
            throw reader.createException(Component.text("Entity type selection is invalid."));
        }
        return Optional.of(predicate);
    }
}
