/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.IPluginInfo;
import io.github.nucleuspowered.nucleus.core.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IModuleReporter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.command.exception.CommandException;

import java.util.Collection;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

@Command(
        aliases = "nucleus",
        basePermission = CorePermissions.BASE_NUCLEUS,
        commandDescriptionKey = "nucleus",
        prefixAliasesWithN = false
)
public class NucleusCommand implements ICommandExecutor {

    private final IModuleReporter reporter;
    private final Component version;

    @Inject
    public NucleusCommand(final IModuleReporter reporter, final IPluginInfo pluginInfo) {
        this.reporter = reporter;
        this.version = Component.text()
                .color(NamedTextColor.GREEN)
                .content(pluginInfo.name() + " version " + pluginInfo.version() + " (built from commit " + pluginInfo.gitHash() + ")")
                .build();
    }
    @Nullable private Component modules = null;

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        if (this.modules == null) {
            final TextComponent.Builder tb = Component.text().content("Modules: ").color(NamedTextColor.GREEN);
            final Collection<String> modules = this.reporter.discoveredModules().stream().sorted().collect(Collectors.toList());
            final Collection<String> enabled = this.reporter.enabledModules().stream().map(ModuleContainer::getId).collect(Collectors.toList());
            final Collection<Component> toJoin = modules.stream()
                    .map(x -> Component.text(x, enabled.contains(x) ? NamedTextColor.GREEN : NamedTextColor.RED))
                    .collect(Collectors.toList());
            tb.append(Component.join(Component.text(", ", NamedTextColor.GREEN), toJoin));
            this.modules = tb.append(Component.text(".", NamedTextColor.GREEN)).build();
        }

        context.sendMessageText(this.version);
        context.sendMessageText(this.modules);
        return context.successResult();
    }
}
