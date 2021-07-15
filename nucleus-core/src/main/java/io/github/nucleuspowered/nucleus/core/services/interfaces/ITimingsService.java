/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

public interface ITimingsService {

    ITiming of(String name);

    interface ITiming extends AutoCloseable {

        ITiming start();

        void stop();

        @Override
        default void close() {
            this.stop();
        }

    }

}
