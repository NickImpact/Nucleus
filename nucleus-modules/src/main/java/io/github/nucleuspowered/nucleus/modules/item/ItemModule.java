/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.item.commands.EnchantCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.MoreCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.RepairCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.ShowAttributesCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.SkullCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.TrashCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.UnsignBookCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.itemname.ItemNameClearCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.itemname.ItemNameCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.itemname.ItemNameSetCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.lore.LoreAddCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.lore.LoreClearCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.lore.LoreCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.lore.LoreDeleteCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.lore.LoreEditCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.lore.LoreInsertCommand;
import io.github.nucleuspowered.nucleus.modules.item.commands.lore.LoreSetCommand;
import io.github.nucleuspowered.nucleus.modules.item.config.ItemConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class ItemModule implements IModule.Configurable<ItemConfig> {

    public static final String ID = "item";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                ItemNameClearCommand.class,
                ItemNameCommand.class,
                ItemNameSetCommand.class,
                LoreAddCommand.class,
                LoreCommand.class,
                LoreClearCommand.class,
                LoreDeleteCommand.class,
                LoreEditCommand.class,
                LoreInsertCommand.class,
                LoreSetCommand.class,
                EnchantCommand.class,
                MoreCommand.class,
                RepairCommand.class,
                ShowAttributesCommand.class,
                SkullCommand.class,
                TrashCommand.class,
                UnsignBookCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(ItemPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public Class<ItemConfig> getConfigClass() {
        return ItemConfig.class;
    }
}
