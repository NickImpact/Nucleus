/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.services;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.module.playerinfo.NucleusSeenService;
import io.github.nucleuspowered.nucleus.core.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.core.scaffold.service.annotations.APIService;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerInformationService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@APIService(NucleusSeenService.class)
public class SeenHandler implements NucleusSeenService, ServiceBase {

    private final INucleusServiceCollection serviceCollection;
    private final Map<String, List<SeenInformationProvider>> pluginInformationProviders = new TreeMap<>();

    @Inject
    public SeenHandler(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
    }

    @Override
    public void register(@NonNull final PluginContainer plugin, @NonNull final SeenInformationProvider seenInformationProvider) throws IllegalArgumentException {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(seenInformationProvider);

        final String name = plugin.metadata().name().orElseGet(plugin.metadata()::id);
        final List<SeenInformationProvider> providers;
        if (this.pluginInformationProviders.containsKey(name)) {
            providers = this.pluginInformationProviders.get(name);
        } else {
            providers = new ArrayList<>();
            this.pluginInformationProviders.put(name, providers);
        }

        providers.add(seenInformationProvider);
    }

    public List<Component> getText(final CommandCause requester, final User user) {
        final List<Component> information = new ArrayList<>();

        final Collection<IPlayerInformationService.Provider> providers = this.serviceCollection.playerInformationService().getProviders();
        for (final IPlayerInformationService.Provider provider : providers) {
            provider.get(user, requester, this.serviceCollection).ifPresent(information::add);
        }

        for (final Map.Entry<String, List<SeenInformationProvider>> entry : this.pluginInformationProviders.entrySet()) {
            entry.getValue().stream().filter(sip -> sip.hasPermission(requester, user.uniqueId())).forEach(sip -> {
                final Collection<Component> input = sip.getInformation(requester, user.uniqueId());
                if (input != null && !input.isEmpty()) {
                    if (information.isEmpty()) {
                        information.add(Component.empty());
                        information.add(Component.text("-----"));
                        information.add(this.serviceCollection.messageProvider().getMessageFor(requester.audience(), "seen.header.plugins"));
                        information.add(Component.text("-----"));
                    }

                    information.add(Component.empty());
                    information.add(Component.text(entry.getKey() + ":", NamedTextColor.AQUA));
                    information.addAll(input);
                }
            });
        }

        return information;
    }
}
