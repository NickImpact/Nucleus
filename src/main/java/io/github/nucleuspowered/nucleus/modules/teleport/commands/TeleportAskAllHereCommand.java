/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.docgen.annotations.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.modules.teleport.events.RequestEvent;
import io.github.nucleuspowered.nucleus.modules.teleport.handlers.TeleportHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Permissions(prefix = "teleport")
@NoModifiers
@NonnullByDefault
@RegisterCommand({"tpaall", "tpaskall"})
@RunAsync
@EssentialsEquivalent({"tpaall"})
public class TeleportAskAllHereCommand extends AbstractCommand<Player> {

    private final TeleportHandler tpHandler;

    @Inject
    public TeleportAskAllHereCommand(TeleportHandler tpHandler) {
        this.tpHandler = tpHandler;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.flags().flag("f").buildWith(GenericArguments.none())};
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        Cause cause = Cause.of(NamedCause.owner(src));
        List<Player> cancelled = Lists.newArrayList();
        Sponge.getServer().getOnlinePlayers().forEach(x -> {
            if (x.equals(src)) {
                return;
            }

            // Before we do all this, check the event.
            RequestEvent.PlayerToCause event = new RequestEvent.PlayerToCause(cause, x);
            if (Sponge.getEventManager().post(event)) {
                cancelled.add(x);
                return;
            }

            TeleportHandler.TeleportBuilder tb = tpHandler.getBuilder().setFrom(x).setTo(src).setSafe(!args.<Boolean>getOne("f").orElse(false))
                    .setBypassToggle(true).setSilentSource(true);
            tpHandler.addAskQuestion(x.getUniqueId(), new TeleportHandler.TeleportPrep(Instant.now().plus(30, ChronoUnit.SECONDS), null, 0, tb));

            x.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tpahere.question", src.getName()));

            x.sendMessage(tpHandler.getAcceptDenyMessage());
        });

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tpaall.success"));
        if (!cancelled.isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tpall.cancelled",
                cancelled.stream().map(x -> x.getName()).collect(Collectors.joining(", "))));
        }

        return CommandResult.success();
    }
}
