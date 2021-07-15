/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.playerinformation;

import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerInformationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Singleton
public class PlayerInformationService implements IPlayerInformationService {

    private final List<Provider> providers = new ArrayList<>();

    @Override
    public void registerProvider(final Provider provider) {
        this.providers.add(provider);
    }

    @Override public Collection<Provider> getProviders() {
        return Collections.unmodifiableList(this.providers);
    }
}
