/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.compatibility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.core.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.core.services.interfaces.ICompatibilityService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IModuleReporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class CompatibilityService implements ICompatibilityService {

    private final IModuleReporter moduleReporter;

    private final List<CompatibilityMessages> messages = new ArrayList<>();
    private List<CompatibilityMessages> applicableMessages = null;

    @Inject
    public CompatibilityService(final IModuleReporter moduleReporter) {
        this.moduleReporter = moduleReporter;
    }

    @Override
    public Collection<CompatibilityMessages> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    @Override
    public Collection<CompatibilityMessages> getApplicableMessages() {
        if (this.messages.isEmpty()) {
            return Collections.emptyList();
        }

        if (this.applicableMessages == null) {
            this.applicableMessages = this.applyMessages();
        }

        return this.applicableMessages;
    }

    @Override
    public void set(final JsonArray jsonArray) {
        if (this.messages.isEmpty()) {
            for (final JsonElement el : jsonArray) {
                try {
                    final JsonObject obj = el.getAsJsonObject();
                    List<String> l = null;
                    if (obj.has("modules")) {
                        l = new ArrayList<>();
                        for (final JsonElement element : obj.getAsJsonArray("modules")) {
                            l.add(el.getAsString());
                        }
                    }
                    this.messages.add(
                            new Message(
                                    obj.get("modid").getAsString(),
                                    this.get(obj.get("severity").getAsInt()),
                                    obj.get("symptom").getAsString(),
                                    obj.get("message").getAsString(),
                                    obj.get("resolution").getAsString(),
                                    l
                            )
                    );
                } catch (final Exception e) {
                    // ignore
                }
            }
        }
    }

    private List<CompatibilityMessages> applyMessages() {
        final Collection<String> id = this.moduleReporter.enabledModules().stream().map(ModuleContainer::getId).collect(Collectors.toList());
        return this.messages
                .stream()
                .filter(x -> id.contains(x.getModId()))
                .collect(Collectors.toList());
    }

    private ICompatibilityService.Severity get(final int i) {
        switch (i) {
            case 3:
                return Severity.CRITICAL;
            case 2:
                return Severity.MAJOR;
            case 1:
                return Severity.MINOR;
            case 0:
            default:
                return Severity.INFORMATIONAL;
        }
    }
}
