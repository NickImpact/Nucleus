/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.infoprovider;

import io.github.nucleuspowered.nucleus.modules.message.MessageKeys;
import io.github.nucleuspowered.nucleus.modules.message.MessagePermissions;
import io.github.nucleuspowered.nucleus.modules.message.services.MessageHandler;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.playerinformation.NucleusProvider;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageInfoProvider implements NucleusProvider {

    @Override
    public String getCategory() {
        return "message";
    }

    @Override
    public Optional<Component> get(final User user, final CommandCause source, final INucleusServiceCollection serviceCollection) {
        if (serviceCollection.permissionService().hasPermission(source, MessagePermissions.BASE_SOCIALSPY)) {
            final MessageHandler handler = serviceCollection.getServiceUnchecked(MessageHandler.class);
            final boolean socialSpy = handler.isSocialSpy(user.uniqueId());
            final boolean msgToggle = serviceCollection.userPreferenceService()
                    .getUnwrapped(user.uniqueId(), MessageKeys.MESSAGE_TOGGLE);
            final IMessageProviderService mp = serviceCollection.messageProvider();
            final String yesOrNo = socialSpy ? "standard.yesno.true" : "standard.yesno.false";
            final List<Component> lt = new ArrayList<>();
            lt.add(mp.getMessageFor(source.audience(), "seen.socialspy", mp.getMessageFor(source.audience(), yesOrNo)));

            /*this.serviceCollection.moduleConfigProvider()
                    .getModuleConfig(MessageConfig.class)*/
            lt.add(
                    mp.getMessageFor(source.audience(),
                            "seen.socialspylevel",
                            serviceCollection.permissionService()
                                    .getPositiveIntOptionFromSubject(user, MessagePermissions.SOCIALSPY_LEVEL_KEY).orElse(0))
            );

            final String msgToggleText = msgToggle ? "standard.yesno.true" : "standard.yesno.false";
            lt.add(mp.getMessageFor(source.audience(), "seen.msgtoggle", mp.getMessageFor(source.audience(), msgToggleText)));

            return Optional.of(Component.join(Component.newline(), lt));
        }

        return Optional.empty();
    }
}
