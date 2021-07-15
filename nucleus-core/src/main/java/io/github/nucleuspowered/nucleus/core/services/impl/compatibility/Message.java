/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.compatibility;

import io.github.nucleuspowered.nucleus.core.services.interfaces.ICompatibilityService;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;

public class Message implements ICompatibilityService.CompatibilityMessages {

    private final String modId;
    private final ICompatibilityService.Severity severity;
    private final String symptom;
    private final String message;
    private final String resolution;
    private final Collection<String> modules;

    public Message(final String modId,
            final ICompatibilityService.Severity severity,
            final String symptom,
            final String message,
            final String resolution,
            @Nullable final Collection<String> modules) {
        this.modId = modId;
        this.severity = severity;
        this.symptom = symptom;
        this.message = message;
        this.resolution = resolution;
        this.modules = modules == null ? Collections.emptySet() : Collections.unmodifiableCollection(modules);
    }

    @Override
    public String getModId() {
        return this.modId;
    }

    @Override public Collection<String> getModules() {
        return this.modules;
    }

    @Override
    public ICompatibilityService.Severity getSeverity() {
        return this.severity;
    }

    @Override
    public String getSymptom() {
        return this.symptom;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getResolution() {
        return this.resolution;
    }
}
