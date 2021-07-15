/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.parameters;

import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import io.github.nucleuspowered.nucleus.modules.warp.WarpPermissions;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
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
import java.util.stream.Collectors;

public final class WarpParameter implements ValueParameter<Warp> {

    private final IPermissionService permissionService;
    private final IMessageProviderService messageProviderService;
    private final WarpService warpService;
    private final boolean checkPermission;

    public WarpParameter(final IPermissionService permissionService, final IMessageProviderService messageProviderService,
            final WarpService warpService, final boolean checkPermission) {
        this.permissionService = permissionService;
        this.messageProviderService = messageProviderService;
        this.warpService = warpService;
        this.checkPermission = checkPermission;
    }

    @Override public List<CommandCompletion> complete(final CommandContext context, final String currentInput) {
        return this.warpService.getWarpNames().stream()
            .filter(s -> s.startsWith(currentInput))
            .filter(s -> !this.checkPermission || this.checkPermission(context.cause(), s))
            .map(CommandCompletion::of)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends Warp> parseValue(final Parameter.Key<? super Warp> parameterKey, final ArgumentReader.Mutable reader,
            final CommandContext.Builder context)
            throws ArgumentParseException {
        final String warp = reader.parseString().toLowerCase();
        if (!this.warpService.warpExists(warp)) {
            throw reader.createException(this.messageProviderService.getMessageFor(context.cause().audience(), "args.warps.noexist"));
        }

        if (this.checkPermission && !this.checkPermission(context, warp) && !this.checkPermission(context, warp.toLowerCase())) {
            throw reader.createException(this.messageProviderService.getMessageFor(context.cause().audience(), "args.warps.noperms"));
        }

        final Optional<Warp> optionalWarp = this.warpService.getWarp(warp);
        if (optionalWarp.isPresent()) {
            return optionalWarp;
        }
        throw reader.createException(this.messageProviderService.getMessageFor(context.cause().audience(), "args.warps.notavailable"));
    }

    private boolean checkPermission(final Subject src, final String name) {
        // No permissions, no entry!
        return this.permissionService.hasPermission(src, WarpPermissions.getWarpPermission(name));
    }
}
