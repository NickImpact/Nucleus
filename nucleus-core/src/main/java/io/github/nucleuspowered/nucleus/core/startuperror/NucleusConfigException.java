/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.startuperror;

import org.spongepowered.configurate.ConfigurateException;

public final class NucleusConfigException extends Exception {

    private final String fileName;
    private final ConfigurateException wrapped;
    private final boolean docgen;

    public NucleusConfigException(final String message, final String fileName, final boolean docgen, final ConfigurateException exception) {
        super(message, exception);
        this.fileName = fileName;
        this.wrapped = exception;
        this.docgen = docgen;
    }

    public String getFileName() {
        return this.fileName;
    }

    public ConfigurateException getWrapped() {
        return this.wrapped;
    }

    public boolean isDocgen() {
        return this.docgen;
    }
}
