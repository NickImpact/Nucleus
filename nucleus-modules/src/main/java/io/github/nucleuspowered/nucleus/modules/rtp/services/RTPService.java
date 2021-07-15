/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp.services;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.module.rtp.NucleusRTPService;
import io.github.nucleuspowered.nucleus.api.module.rtp.kernel.RTPKernel;
import io.github.nucleuspowered.nucleus.api.module.rtp.kernel.RTPKernels;
import io.github.nucleuspowered.nucleus.core.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.core.scaffold.service.annotations.APIService;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.modules.rtp.config.RTPConfig;
import io.github.nucleuspowered.nucleus.modules.rtp.options.RTPOptionsBuilder;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@APIService(NucleusRTPService.class)
public class RTPService implements NucleusRTPService, IReloadableService.Reloadable, ServiceBase {

    private final Logger logger;
    private RTPConfig config = new RTPConfig();
    @Nullable private RTPKernel lazyLoadedKernel = null;
    private final Map<RTPConfig.PerWorldRTPConfig, RTPKernel> perWorldLazyLoadedKernel = new WeakHashMap<>();

    @Inject
    public RTPService(final INucleusServiceCollection serviceCollection) {
        this.logger = serviceCollection.logger();
    }

    @Override
    public RTPOptions options(@Nullable final ServerWorldProperties world) {
        @Nullable final String name = world == null ? null : world.key().asString();
        return new io.github.nucleuspowered.nucleus.modules.rtp.options.RTPOptions(this.config, name);
    }

    @Override
    public RTPOptions.Builder optionsBuilder() {
        return new RTPOptionsBuilder();
    }

    @Override
    public RTPKernel getDefaultKernel() {
        if (this.lazyLoadedKernel == null) {
            // does the kernel exist?
            final String kernelId = this.config.getDefaultRTPKernel();
            final String idToUse = kernelId.contains(":") ? kernelId : "nucleus:" + kernelId;
            final Optional<RTPKernel> rtpKernel = RTPKernels.REGISTRY_TYPE.find().flatMap(x -> x.findValue(ResourceKey.resolve(idToUse)));
            if (!rtpKernel.isPresent()) {
                this.logger.warn("Kernel with ID {} could not be found. Falling back to the default.", RTPKernels.Identifiers.DEFAULT.asString());
                this.lazyLoadedKernel = RTPKernels.DEFAULT.get();
            } else {
                this.lazyLoadedKernel = rtpKernel.get();
            }
        }

        return this.lazyLoadedKernel;
    }

    @Override public RTPKernel getKernel(final ServerWorldProperties world) {
        return this.getKernel(world.key().asString());
    }

    @Override public RTPKernel getKernel(final ServerWorld world) {
        return this.getKernel(world.key().asString());
    }

    @Override public RTPKernel getKernel(final String world) {
        return this.config.get(world).map(x -> {
            final RTPKernel kernel = this.perWorldLazyLoadedKernel.get(x);
            if (kernel == null) {
                // does the kernel exist?
                final String kernelId = this.config.getDefaultRTPKernel();
                final String idToUse = kernelId.contains(":") ? kernelId : "nucleus:" + kernelId;
                final Optional<RTPKernel> rtpKernel = RTPKernels.REGISTRY_TYPE.find().flatMap(y -> y.findValue(ResourceKey.resolve(idToUse)));
                if (!rtpKernel.isPresent()) {
                    this.logger.warn("Kernel with ID {} for world {} could not be found. Falling back to the default.",
                            kernelId, world);
                    this.perWorldLazyLoadedKernel.put(x, RTPKernels.DEFAULT.get());
                } else {
                    this.perWorldLazyLoadedKernel.put(x, rtpKernel.get());
                }
            }

            return this.perWorldLazyLoadedKernel.get(x);
        }).orElseGet(this::getDefaultKernel);
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        // create the new RTPOptions
        this.config = serviceCollection.configProvider().getModuleConfig(RTPConfig.class);
    }
}
