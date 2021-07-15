/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.bootstrap.error;

import io.github.nucleuspowered.nucleus.core.IPluginInfo;
import io.github.nucleuspowered.nucleus.core.startuperror.NucleusErrorHandler;
import io.github.nucleuspowered.nucleus.core.util.PrettyPrinter;
import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;

public final class ConfigErrorHandler extends NucleusErrorHandler {

    private static final String DEBUG_CONFIG_ERROR = "nucleus.config_stacktrace";

    private final String file;

    public ConfigErrorHandler(
            final PluginContainer pluginContainer,
            final Throwable throwable,
            final boolean shouldShutDown,
            final Logger logger,
            final String file,
            final IPluginInfo pluginInfo) {
        super(pluginContainer, throwable, shouldShutDown, logger, pluginInfo);
        this.file = file;
    }

    @Override
    public String getTitle() {
        return "NUCLEUS CONFIGURATION FAILED TO LOAD (" + this.file + ")";
    }

    @Override
    protected void printStackTrace(final PrettyPrinter prettyPrinter) {
        if (System.getProperty(ConfigErrorHandler.DEBUG_CONFIG_ERROR, "false").equals("true")) {
            super.printStackTrace(prettyPrinter);
        }
    }

    @Override
    protected void createPostStackTraceMessage(final PrettyPrinter prettyPrinter) {
        // it's a config error.
    }

    @Override
    protected void createTopLevelMessage(final PrettyPrinter prettyPrinter) {
        final List<String> messages = new ArrayList<>();
        Throwable throwable = this.capturedThrowable;
        do {
            if (throwable.getMessage() != null) {
                messages.add(String.format("* %s", throwable.getMessage()));
            }
            throwable = throwable.getCause();
        } while (throwable != null);
        prettyPrinter.add("One of your configuration files (%s) is broken and could not be read.", this.file);
        prettyPrinter.add();
        if (messages.isEmpty()) {
            prettyPrinter.add("No error messages were returned");
        } else {
            prettyPrinter.add("The following error messages were returned:");
            for (final String message : messages) {
                prettyPrinter.add(message);
            }
        }
        prettyPrinter.add();
        prettyPrinter.add("This is usually because you've added an ID but forgotten to surround the ID with double quotes "
                + "(\").");
        prettyPrinter.add("For example, to add minecraft:stone to the config, it should be added as \"minecraft:stone\"");
        prettyPrinter.add();
        prettyPrinter.add("You will need to fix the configuration file and restart your server.");
    }
}
