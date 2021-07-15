/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp.kernels;

import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.api.module.rtp.NucleusRTPService;

import java.util.Random;

public class KernelHelper {

    public static final KernelHelper INSTANCE = new KernelHelper();

    protected KernelHelper() {}

    private final Random random = new Random();

    public int getRandomBetween(final int min, final int max) {
        return this.random.nextInt(max - min) + min;
    }

    public int randomSign(final int in) {
        return this.random.nextInt(2) == 0 ? -in : in;
    }

    public Vector3d getLocationWithOffset(final Vector3i centre, final NucleusRTPService.RTPOptions options) {
        final int x = this.randomSign(this.getRandomBetween(options.minRadius(), options.maxRadius())) + centre.x();
        final int y = this.getRandomBetween(options.minHeight(), options.maxHeight());
        final int z = this.randomSign(this.getRandomBetween(options.minRadius(), options.maxRadius())) + centre.z();
        return new Vector3d(x, y, z);
    }

}
