/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.configprovider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.core.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.core.guice.ConfigDirectory;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IConfigProvider;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IConfigurateHelper;
import io.leangen.geantyref.TypeToken;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Singleton
public class ConfigProvider implements IConfigProvider {

    private static final String CORE_CONFIG_PATH = "core_config.conf";
    private static final String MODULES_CONFIG_PATH = "module_config.conf";

    private final HoconConfigurationLoader coreLoader;
    private final HoconConfigurationLoader modulesLoader;
    private final Logger logger;

    @Nullable private CommentedConfigurationNode coreNode = null;
    @Nullable private CommentedConfigurationNode moduleNode = null;
    @Nullable private CoreConfig coreConfig = null;
    private final Map<String, Class<?>> moduleConfigs = new HashMap<>();
    private final Map<Class<?>, Collection<ConfigurationTransformation>> moduleTransformations = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> providers = new HashMap<>();
    private final Map<Class<?>, Object> cachedConfig = new HashMap<>();

    @Inject
    public ConfigProvider(@ConfigDirectory final Path configPath, final IConfigurateHelper configurateHelper, final Logger logger) {
        this.logger = logger;
        this.coreLoader = ConfigProvider.getLoader(configurateHelper, configPath.resolve(ConfigProvider.CORE_CONFIG_PATH));
        this.modulesLoader = ConfigProvider.getLoader(configurateHelper, configPath.resolve(ConfigProvider.MODULES_CONFIG_PATH));
    }

    @Override
    public CoreConfig getCoreConfig() {
        return this.coreConfig;
    }

    @Override
    public <T> void registerModuleConfig(final String moduleId,
            final Class<T> typeOfConfig,
            final Supplier<T> creator,
            final Collection<ConfigurationTransformation> configurationTransformationCollection) {
        Objects.requireNonNull(typeOfConfig, "Module ID: " + moduleId + " has a null config type!");
        if (this.providers.containsKey(typeOfConfig) || this.moduleConfigs.containsKey(moduleId)) {
            throw new IllegalStateException("Cannot register type or module more than once (type of config " + typeOfConfig.getSimpleName() +
                    ", id " + moduleId + ")!");
        }

        this.providers.put(typeOfConfig, creator);
        this.moduleConfigs.put(moduleId, typeOfConfig);
        this.moduleTransformations.put(typeOfConfig, configurationTransformationCollection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getModuleConfig(final Class<T> configType) throws IllegalArgumentException {
        if (configType == CoreConfig.class) {
            return (T) this.getCoreConfig();
        }
        if (this.cachedConfig.containsKey(configType)) {
            return (T) this.cachedConfig.get(configType);
        }
        if (this.providers.containsKey(configType)) {
            final T result = (T) this.providers.get(configType).get();
            this.cachedConfig.put(configType, result);
            return result;
        }

        throw new IllegalArgumentException(configType.getSimpleName() + " does not exist");
    }

    @Override
    public Map<String, Class<?>> getModuleToConfigType() {
        return Collections.unmodifiableMap(new HashMap<>(this.moduleConfigs));
    }

    @Override
    public <T> T getDefaultModuleConfig(final Class<T> configType) throws IllegalArgumentException {
        if (this.providers.containsKey(configType)) {
            try {
                return configType.getDeclaredConstructor().newInstance();
            } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new IllegalArgumentException("Could not instantiate", e);
            }
        }

        throw new IllegalArgumentException(configType.getSimpleName() + " does not exist");
    }

    public void loadCore(final boolean actualLoad) throws ConfigurateException {
        if (this.coreNode == null || actualLoad) {
            this.coreNode = this.coreLoader.load();
        }
        this.coreConfig = this.coreNode.get(io.leangen.geantyref.TypeToken.get(CoreConfig.class), (Supplier<CoreConfig>) CoreConfig::new);
    }

    public void loadModules(final boolean actualLoad) throws ConfigurateException {
        if (this.moduleNode == null || actualLoad) {
            this.moduleNode = this.modulesLoader.load();
        }
        this.cachedConfig.clear();
        for (final Map.Entry<String, Class<?>> moduleClass : this.moduleConfigs.entrySet()) {
            try {
                this.cachedConfig.put(moduleClass.getValue(), this.get(this.moduleNode.node(moduleClass.getKey()), moduleClass.getValue()));
            } catch (final Exception exception) {
                this.logger.error("Could not load module config section for '" + moduleClass.getKey() + "' - using default.", exception);
                this.cachedConfig.put(moduleClass.getValue(), this.moduleNode.node(moduleClass.getKey()));
            }
        }
    }

    @Override
    public void prepareCoreConfig(final Collection<ConfigurationTransformation> coreConfigurationTransformations)
            throws ConfigurateException {
        final CommentedConfigurationNode coreToSave = this.coreLoader.load();
        for (final ConfigurationTransformation transformation : coreConfigurationTransformations) {
            transformation.apply(coreToSave);
        }
        coreToSave.mergeFrom(this.coreLoader.createNode().set(TypeToken.get(CoreConfig.class), new CoreConfig()));
        this.coreLoader.save(coreToSave);
        this.coreNode = coreToSave;
        this.loadCore(false);
    }

    @Override
    public void prepareModuleConfig() throws ConfigurateException {
        final CommentedConfigurationNode modulesToSave = this.modulesLoader.load();
        final CommentedConfigurationNode defaults = this.modulesLoader.createNode();
        for (final Map.Entry<String, Class<?>> moduleClass : this.moduleConfigs.entrySet()) {
            // Transform existing nodes
            final Collection<ConfigurationTransformation> transformations = this.moduleTransformations.get(moduleClass.getValue());
            if (transformations != null) {
                for (final ConfigurationTransformation transformation : transformations) {
                    transformation.apply(modulesToSave.node(moduleClass.getKey()));
                }
            }

            // Default nodes.
            this.set(defaults.node(moduleClass.getKey()), moduleClass.getValue());
        }
        modulesToSave.mergeFrom(defaults);
        this.modulesLoader.save(modulesToSave);
        this.moduleNode = modulesToSave;
        this.loadModules(false);
    }

    @Override public String getCoreConfigFileName() {
        return ConfigProvider.CORE_CONFIG_PATH;
    }

    @Override public String getModuleConfigFileName() {
        return ConfigProvider.MODULES_CONFIG_PATH;
    }

    @SuppressWarnings("unchecked")
    private <T> T get(final CommentedConfigurationNode node, final Class<T> clazz) throws ConfigurateException {
        return node.get(TypeToken.get(clazz), (Supplier<T>) () -> (T) this.providers.get(clazz).get());
    }

    @SuppressWarnings("unchecked")
    private <T> void set(final CommentedConfigurationNode node, final Class<T> clazz) throws ConfigurateException {
        node.set(TypeToken.get(clazz), (T) this.providers.get(clazz).get());
    }

    @Override
    public void reload() {
        this.cachedConfig.clear();
        try {
            this.loadCore(true);
        } catch (final ConfigurateException e) {
            this.logger.error("Could not load core configuration file. Using default.", e);
        }

        try {
            this.loadModules(true);
        } catch (final ConfigurateException e) {
            this.logger.error("Could not load module configuration file. Using default.", e);
        }
    }

    private static HoconConfigurationLoader getLoader(final IConfigurateHelper helper, final Path path) {
        return HoconConfigurationLoader.builder()
                .defaultOptions(helper.getOptions())
                .path(path)
                .build();
    }
}
