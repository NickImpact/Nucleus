/*
 * This file is part of QuickStart, licensed under the MIT License (MIT). See the LICENCE.txt file
 * at the root of this project for more details.
 */
package uk.co.drnaylor.minecraft.quickstart.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.spongepowered.api.service.permission.Subject;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.NoCooldown;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.NoCost;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.NoWarmup;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.Permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandPermissionHandler {

    private final static Map<Class<? extends CommandBase>, CommandPermissionHandler> serviceRegistry = Maps.newHashMap();

    public static Optional<CommandPermissionHandler> getService(Class<? extends CommandBase> command) {
        return Optional.ofNullable(serviceRegistry.get(command));
    }

    public static Map<String, SuggestedLevel> getPermissions() {
        Map<String, SuggestedLevel> m = new HashMap<>();
        serviceRegistry.values().forEach(x -> m.putAll(x.getSuggestedPermissions()));
        return m;
    }

    public final static String PERMISSIONS_PREFIX = "quickstart.";
    private final Map<String, SuggestedLevel> mssl = Maps.newHashMap();
    private final String prefix;
    private final String base;
    private final String warmup;
    private final String cooldown;
    private final String cost;

    public CommandPermissionHandler(CommandBase cb) {
        Permissions c = cb.getClass().getAnnotation(Permissions.class);
        Preconditions.checkNotNull(c);

        StringBuilder sb = new StringBuilder(PERMISSIONS_PREFIX);
        if (!c.root().isEmpty()) {
            sb.append(c.root()).append(".");
        }

        if (c.alias().isEmpty()) {
            sb.append(cb.getAliases()[0]);
        } else {
            sb.append(c.alias());
        }

        sb.append(".");
        if (!c.sub().isEmpty()) {
            sb.append(c.sub()).append(".");
        }

        prefix = sb.toString();

        base = prefix + "base";
        mssl.put(base, c.suggestedLevel());

        warmup = prefix + "exempt.warmup";
        cooldown = prefix + "exempt.cooldown";
        cost = prefix + "exempt.cost";

        if (!cb.getClass().isAnnotationPresent(NoWarmup.class)) {
            mssl.put(warmup, SuggestedLevel.ADMIN);
        }

        if (!cb.getClass().isAnnotationPresent(NoCooldown.class)) {
            mssl.put(cooldown, SuggestedLevel.ADMIN);
        }

        if (!cb.getClass().isAnnotationPresent(NoCost.class)) {
            mssl.put(cost, SuggestedLevel.ADMIN);
        }

        serviceRegistry.put(cb.getClass(), this);
    }

    public boolean testBase(Subject src) {
        return src.hasPermission(base);
    }

    public boolean testWarmupExempt(Subject src) {
        return src.hasPermission(warmup);
    }

    public boolean testCooldownExempt(Subject src) {
        return src.hasPermission(cooldown);
    }

    public boolean testCostExempt(Subject src) {
        return src.hasPermission(cost);
    }

    public void registerPermssionSuffix(String suffix, SuggestedLevel level) {
        this.mssl.put(prefix + suffix, level);
    }

    public void registerPermssion(String permission, SuggestedLevel level) {
        this.mssl.put(permission, level);
    }

    public boolean testSuffix(Subject src, String suffix) {
        return src.hasPermission(prefix + suffix);
    }

    public String getPermissionWithSuffix(String suffix) {
        return prefix + suffix;
    }

    public Map<String, SuggestedLevel> getSuggestedPermissions() {
        return ImmutableMap.copyOf(mssl);
    }

    public enum SuggestedLevel {
        ADMIN("admin"),
        MOD("staff"),
        USER("user");

        public final String role;

        SuggestedLevel(String role) {
            this.role = role;
        }
    }
}
