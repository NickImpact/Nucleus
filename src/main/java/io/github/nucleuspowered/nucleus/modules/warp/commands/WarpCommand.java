/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.data.Warp;
import io.github.nucleuspowered.nucleus.argumentparsers.WarpArgument;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.warp.config.WarpConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.warp.event.UseWarpEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Allows a user to warp to the specified warp.
 *
 * Command Usage: /warp [warp] Permission: quickstart.warp.base
 *
 * <p>
 * If <code>warp.separate-permissions</code> = <code>true</code> in the commands
 * config, also requires <code>plugin.warps.[warpname]</code> permission, or
 * the NucleusPlugin admin permission.
 * </p>
 *
 * <p>
 *     NoCost is applied, as this is handled via the main config file.
 * </p>
 */
@Permissions(suggestedLevel = SuggestedLevel.USER)
@RegisterCommand(value = "warp")
@NoCost
public class WarpCommand extends AbstractCommand<Player> {

    static final String warpNameArg = Nucleus.getNucleus().getMessageProvider().getMessageWithFormat("args.name.warpname");

    @Inject private WarpConfigAdapter adapter;

    @Override
    protected Map<String, PermissionInformation> permissionsToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put(PermissionRegistry.PERMISSIONS_PREFIX + "warps",
                new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permissions.warps"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.onlyOne(GenericArguments
                        .optionalWeak(GenericArguments.flags()
                                .flag("y", "a", "-accept")
                                .flag("f", "-force").setAnchorFlags(false).buildWith(GenericArguments.none()))),
                GenericArguments.onlyOne(new WarpArgument(Text.of(warpNameArg), adapter, true))};
    }

    @Override
    protected ContinueMode preProcessChecks(Player source, CommandContext args) {
        if (!plugin.getEconHelper().economyServiceExists() || permissions.testCostExempt(source) || args.hasAny("y")) {
            return ContinueMode.CONTINUE;
        }

        Warp wd = args.<Warp>getOne(warpNameArg).get();
        Optional<Double> i = wd.getCost();
        double cost = i.orElseGet(() -> adapter.getNodeOrDefault().getDefaultWarpCost());

        if (cost <= 0) {
            return ContinueMode.CONTINUE;
        }

        String costWithUnit = plugin.getEconHelper().getCurrencySymbol(cost);
        if (plugin.getEconHelper().hasBalance(source, cost)) {
            String command = String.format("/warp -y %s", wd.getName());
            source.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.cost.details", wd.getName(), costWithUnit));
            source.sendMessage(
            plugin.getMessageProvider().getTextMessageWithFormat("command.warp.cost.clickaccept").toBuilder()
                    .onClick(TextActions.runCommand(command)).onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.cost.clickhover", command)))
                    .append(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.cost.alt")).build());
        } else {
            source.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.cost.nomoney", wd.getName(), costWithUnit));
        }

        return ContinueMode.STOP;
    }

    @Override
    public CommandResult executeCommand(Player pl, CommandContext args) throws Exception {
        // Permission checks are done by the parser.
        Warp wd = args.<Warp>getOne(warpNameArg).get();

        UseWarpEvent event = new UseWarpEvent(Cause.of(NamedCause.owner(pl)), wd.getName(), pl, wd.getLocation().get());
        if (Sponge.getEventManager().post(event)) {
            throw new ReturnMessageException(event.getCancelMessage().orElseGet(() ->
                plugin.getMessageProvider().getTextMessageWithFormat("nucleus.eventcancelled")
            ));
        }

        Optional<Double> i = wd.getCost();
        double cost = i.orElseGet(() -> adapter.getNodeOrDefault().getDefaultWarpCost());

        boolean chg = false;
        if (plugin.getEconHelper().economyServiceExists() && !permissions.testCostExempt(pl) && cost > 0) {
            if (plugin.getEconHelper().withdrawFromPlayer(pl, cost, false)) {
                chg = true;
            } else {
                pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.cost.nomoney", wd.getName(), plugin.getEconHelper().getCurrencySymbol(cost)));
                return CommandResult.empty();
            }
        }

        // We have a warp data, warp them.
        pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.start", wd.getName()));

        // Warp them.
        boolean isSafe = !args.getOne("f").isPresent() && adapter.getNodeOrDefault().isSafeTeleport();
        if (!plugin.getTeleportHandler().teleportPlayer(pl, wd.getLocation().get(), wd.getRotation(), isSafe)) {
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.nosafe"));

            if (chg) {
                plugin.getEconHelper().depositInPlayer(pl, cost, false);
            }
            // Don't add the cooldown if enabled.
            return CommandResult.empty();
        }

        if (chg) {
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.cost.charged", plugin.getEconHelper().getCurrencySymbol(cost)));
        }

        return CommandResult.success();
    }
}
