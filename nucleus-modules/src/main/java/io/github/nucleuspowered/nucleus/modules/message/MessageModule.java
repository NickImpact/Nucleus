/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.core.event.NucleusRegisterPreferenceKeyEvent;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.core.services.impl.playerinformation.NucleusProvider;
import io.github.nucleuspowered.nucleus.core.services.impl.userprefs.UserPreferenceService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IUserPreferenceService;
import io.github.nucleuspowered.nucleus.modules.message.commands.HelpOpCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.MessageCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.MsgToggleCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.ReplyCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.SocialSpyCommand;
import io.github.nucleuspowered.nucleus.modules.message.config.MessageConfig;
import io.github.nucleuspowered.nucleus.modules.message.infoprovider.MessageInfoProvider;
import io.github.nucleuspowered.nucleus.modules.message.listener.MessageListener;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.modules.message.services.MessageHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterRegistryValueEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class MessageModule implements IModule.Configurable<MessageConfig> {

    public static final String ID = "message";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(MessageHandler.class, new MessageHandler(serviceCollection), false);
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                HelpOpCommand.class,
                MessageCommand.class,
                MsgToggleCommand.class,
                ReplyCommand.class,
                SocialSpyCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(MessagePermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(MessageListener.class);
    }

    @Override
    public Class<MessageConfig> getConfigClass() {
        return MessageConfig.class;
    }

    @Listener
    public void onPreferenceKeyRegistration(final NucleusRegisterPreferenceKeyEvent event) {
        event.register(MessageKeys.MESSAGE_TOGGLE).register(MessageKeys.SOCIAL_SPY);
    }

    @Override
    public Optional<NucleusProvider> getInfoProvider() {
        return Optional.of(new MessageInfoProvider());
    }

}
