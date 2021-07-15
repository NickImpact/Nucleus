/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.parameters;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.service.permission.Subject;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class KitParameter implements ValueParameter<Kit> {

    private final KitService kitService;
    private final IPermissionService permissionService;
    private final IMessageProviderService messageProviderService;
    private final boolean permissionCheck;

    public KitParameter(final INucleusServiceCollection serviceCollection, final KitService kitService, final boolean permissionCheck) {
        this.kitService = kitService;
        this.messageProviderService = serviceCollection.messageProvider();
        this.permissionService = serviceCollection.permissionService();
        this.permissionCheck = permissionCheck;
    }

    @Override
    public List<CommandCompletion> complete(final CommandContext context, final String currentInput) {
        final boolean showhidden = this.permissionService.hasPermission(context, KitPermissions.KIT_SHOWHIDDEN);
        return this.kitService.getKitNames().stream()
                .filter(s -> s.toLowerCase().startsWith(currentInput.toLowerCase()))
                .limit(20)
                .map(x -> this.kitService.getKit(x).get())
                .filter(x -> this.checkPermission(context, x))
                .filter(x -> this.permissionCheck && (showhidden || !x.isHiddenFromList()))
                .map(x -> CommandCompletion.of(x.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends Kit> parseValue(final Parameter.Key<? super Kit> parameterKey, final ArgumentReader.Mutable reader,
            final CommandContext.Builder context)
            throws ArgumentParseException {
        final String kitName = reader.parseString();
        if (kitName.isEmpty()) {
            throw reader.createException(this.messageProviderService.getMessageFor(context.cause().audience(), "args.kit.noname"));
        }

        final Kit kit = this.kitService.getKit(kitName)
                .orElseThrow(() -> reader.createException(this.messageProviderService.getMessageFor(context.cause().audience(),"args.kit.noexist")));

        if (!this.checkPermission(context.cause(), kit)) {
            throw reader.createException(this.messageProviderService.getMessageFor(context.cause().audience(),"args.kit.noperms"));
        }

        return Optional.of(kit);
    }

    private boolean checkPermission(final Subject src, final Kit kit) {
        if (!this.permissionCheck) {
            return true;
        }

        // No permissions, no entry!
        return this.permissionService.hasPermission(src, KitPermissions.getKitPermission(kit.getName()));
    }
}
