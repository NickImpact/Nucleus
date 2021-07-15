/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.modulereporter;

import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.core.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IModuleReporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Singleton
public final class ModuleReporter implements IModuleReporter {

    private final Collection<String> discoveredModules = new ArrayList<>();
    private final Collection<ModuleContainer> enabledModules = new HashSet<>();
    private final Collection<String> enabledModulesNames = new ArrayList<>();

    @Override
    public Collection<String> discoveredModules() {
        return Collections.unmodifiableCollection(this.discoveredModules);
    }

    @Override
    public Collection<ModuleContainer> enabledModules() {
        return Collections.unmodifiableCollection(this.enabledModules);
    }

    @Override
    public void provideDiscoveredModules(final Collection<String> discoveredModules) {
        if (this.discoveredModules.isEmpty()) {
            this.discoveredModules.addAll(discoveredModules);
        }
    }

    @Override
    public void provideEnabledModule(final ModuleContainer moduleContainer) {
        this.enabledModules.add(moduleContainer);
        this.enabledModulesNames.add(moduleContainer.getId());
    }

    @Override
    public boolean isLoaded(final String module) {
        return this.enabledModulesNames.contains(module);
    }
}
