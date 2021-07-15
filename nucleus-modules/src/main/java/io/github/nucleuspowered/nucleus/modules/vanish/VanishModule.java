/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.api.core.event.NucleusRegisterPreferenceKeyEvent;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.core.services.impl.playerinformation.NucleusProvider;
import io.github.nucleuspowered.nucleus.modules.vanish.commands.ToggleVanishOnLoginCommand;
import io.github.nucleuspowered.nucleus.modules.vanish.commands.VanishCommand;
import io.github.nucleuspowered.nucleus.modules.vanish.config.VanishConfig;
import io.github.nucleuspowered.nucleus.modules.vanish.infoprovider.VanishInfoProvider;
import io.github.nucleuspowered.nucleus.modules.vanish.listener.VanishListener;
import io.github.nucleuspowered.nucleus.modules.vanish.services.VanishService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.util.Identifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class VanishModule implements IModule.Configurable<VanishConfig> {

    public static final String ID = "vanish";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(VanishService.class, new VanishService(serviceCollection), false);
        serviceCollection.placeholderService()
                .registerToken(
                        "vanished",
                        PlaceholderParser.builder()
                                .parser(p -> {
                                    if (p.associatedObject()
                                            .filter(x -> x instanceof Identifiable)
                                            .map(x -> serviceCollection.getServiceUnchecked(VanishService.class).isVanished(((Identifiable) x).uniqueId()))
                                            .orElse(false)) {
                                        return Component.text("[Vanished]", NamedTextColor.GRAY);
                                    }
                                    return Component.empty();
                                }).build()
                );
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                ToggleVanishOnLoginCommand.class,
                VanishCommand.class
        );
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(VanishPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(VanishListener.class);
    }

    @Override public Class<VanishConfig> getConfigClass() {
        return VanishConfig.class;
    }

    @Override public Optional<NucleusProvider> getInfoProvider() {
        return Optional.of(new VanishInfoProvider());
    }

    @Listener
    public void onRegisterNucleusPreferenceKeys(final NucleusRegisterPreferenceKeyEvent event) {
        event.register(VanishKeys.VANISH_ON_LOGIN);
    }

}
