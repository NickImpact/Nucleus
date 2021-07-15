/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.timing;

import io.github.nucleuspowered.nucleus.core.services.interfaces.ITimingsService;

public final class DummyTimingsService implements ITimingsService {

    final static class Holding {
        final static ITiming DUMMY = new ITiming() {
            @Override
            public ITiming start() {
                return this;
            }

            @Override
            public void stop() {
            }
        };
    }

    @Override
    public ITiming of(final String name) {
        return Holding.DUMMY;
    }

}
