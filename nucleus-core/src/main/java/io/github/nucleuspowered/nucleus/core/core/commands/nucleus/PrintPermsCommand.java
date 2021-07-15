/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.commands.nucleus;

import io.github.nucleuspowered.nucleus.core.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.core.commands.NucleusCommand;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.interfaces.data.SuggestedLevel;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Command(
        aliases = "printperms",
        basePermission = CorePermissions.BASE_NUCLEUS_PRINTPERMS,
        commandDescriptionKey = "nucleus.printperms",
        parentCommand = NucleusCommand.class
)
public class PrintPermsCommand implements ICommandExecutor {

    private static List<String> onStream(final Collection<IPermissionService.Metadata> metadata, final SuggestedLevel suggestedLevel) {
        return metadata.stream()
                .filter(x -> x.getSuggestedLevel() == suggestedLevel)
                .map(IPermissionService.Metadata::getPermission)
                .collect(Collectors.toList());
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Collection<IPermissionService.Metadata> l = context.getServiceCollection()
                .permissionService()
                .getAllMetadata();

        final List<String> notsuggested = onStream(l, SuggestedLevel.NONE);
        final List<String> owner = onStream(l, SuggestedLevel.OWNER);
        final List<String> admin = onStream(l, SuggestedLevel.ADMIN);
        final List<String> mod = onStream(l, SuggestedLevel.MOD);
        final List<String> user = onStream(l, SuggestedLevel.USER);

        final String file = "plugin-perms.txt";
        try (final BufferedWriter f = new BufferedWriter(new FileWriter(file))) {

            final Consumer<String> permWriter = x -> {
                try {
                    f.write(x);
                    f.newLine();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            };


            f.write("Not Suggested");
            f.write("-----");
            f.newLine();
            notsuggested.stream().sorted().forEach(permWriter);
            f.newLine();

            f.write("Owner");
            f.write("-----");
            f.newLine();
            owner.stream().sorted().forEach(permWriter);
            f.newLine();

            f.write("Admin");
            f.write("-----");
            f.newLine();

            admin.stream().sorted().forEach(permWriter);
            f.newLine();
            f.write("Mod");
            f.write("-----");
            f.newLine();

            mod.stream().sorted().forEach(permWriter);
            f.newLine();
            f.write("User");
            f.write("-----");
            f.newLine();

            user.stream().sorted().forEach(permWriter);
            f.flush();
        } catch (final IOException e) {
            throw new CommandException(Component.text("File write failed"), e);
        }

        context.sendMessage("command.printperms", file);
        return context.successResult();
    }
}
