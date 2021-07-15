/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandlogger.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ConfigSerializable
public class CommandLoggerConfig {

    @Setting(value = "log-command-source")
    @LocalisedComment("config.commandlogger.source.base")
    private LoggerTargetConfig loggerTarget = new LoggerTargetConfig();

    @Setting(value = "whitelist")
    @LocalisedComment("config.commandlogger.whitelist")
    private boolean isWhitelist = false;

    @Setting(value = "command-filter")
    @LocalisedComment("config.commandlogger.list")
    private List<String> commandsToFilter = new ArrayList<>();

    @Setting(value = "log-to-file")
    @LocalisedComment("config.commandlogger.file")
    private boolean logToFile = false;

    @Setting(value = "cause-enhanced")
    @LocalisedComment("config.commandlogger.causeenhanced")
    private boolean causeEnhanced = true;

    public LoggerTargetConfig getLoggerTarget() {
        return this.loggerTarget;
    }

    public boolean isWhitelist() {
        return this.isWhitelist;
    }

    public List<String> getCommandsToFilter() {
        return Collections.unmodifiableList(this.commandsToFilter);
    }

    public boolean isLogToFile() {
        return this.logToFile;
    }

    public boolean isCauseEnhanced() {
        return this.causeEnhanced;
    }

}
