/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chat.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class ChatConfig {

    @Setting(value = "modify-chat")
    @LocalisedComment("config.chat.modify")
    private boolean modifychat = true;

    @Setting(value = "templates")
    private TemplateConfig templates = new TemplateConfig();

    @Setting(value = "try-to-remove-minecraft-prefix")
    @LocalisedComment("config.chat.hardertoremove")
    private boolean tryRemoveMinecraftPrefix = true;

    @Setting(value = "remove-link-underlines")
    @LocalisedComment("config.chat.removeunderlines")
    private boolean removeBlueUnderline = true;

    @Setting(value = "aggressive-chat-formatting")
    @LocalisedComment("config.chat.aggressive")
    private boolean tryForceFormatting = false;

    @Setting("me-prefix")
    @LocalisedComment("config.chat.meprefix")
    private String mePrefix = "&7* {{displayName}} ";

    @Setting(value = "ignore-other-plugins-when-formatting")
    @LocalisedComment("config.chat.removeother")
    private boolean ignoreOtherPlugins = false;

    @Setting(value = "modify-message")
    @LocalisedComment("config.chat.modifymessage")
    private boolean modifyMessage = true;

    public String getMePrefix() {
        return this.mePrefix;
    }

    public boolean isModifychat() {
        return this.modifychat;
    }

    public boolean isUseGroupTemplates() {
        return this.templates.isUseGroupTemplates();
    }

    public ChatTemplateConfig getDefaultTemplate() {
        return this.templates.getDefaultTemplate();
    }

    public Map<String, ChatTemplateConfig> getGroupTemplates() {
        return Collections.unmodifiableMap(new HashMap<>(this.templates.getGroupTemplates()));
    }

    public boolean isRemoveBlueUnderline() {
        return this.removeBlueUnderline;
    }

    public boolean isTryRemoveMinecraftPrefix() {
        return this.tryRemoveMinecraftPrefix;
    }

    public boolean isTryForceFormatting() {
        return this.tryForceFormatting;
    }

    public boolean isIgnoreOtherPlugins() {
        return this.ignoreOtherPlugins;
    }

    public boolean isModifyMessage() {
        return this.modifyMessage;
    }
}
