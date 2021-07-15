/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.timing;

import co.aikar.timings.Timing;
import co.aikar.timings.TimingsFactory;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.services.interfaces.ITimingsService;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

public final class TimingsService implements ITimingsService {

    private final PluginContainer container;

    @Inject
    public TimingsService(final PluginContainer container) {
        this.container = container;
    }

    @Override
    public ITiming of(final String name) {
        return new AikarTiming(this.container, name);
    }

    final static class AikarTiming implements ITiming {

        private final Timing timing;

        AikarTiming(final PluginContainer container, final String name) {
            this.timing = Sponge.game().factoryProvider().provide(TimingsFactory.class).of(container, name, null);
        }

        @Override
        public ITiming start() {
            this.timing.startTiming();
            return this;
        }

        @Override
        public void stop() {
            this.timing.stopTiming();
        }

    }

}
