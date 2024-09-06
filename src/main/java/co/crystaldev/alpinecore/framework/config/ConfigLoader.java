package co.crystaldev.alpinecore.framework.config;

import co.crystaldev.alpinecore.AlpinePlugin;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The ConfigLoader class is responsible for loading and managing configurations.
 * It allows you to load configurations from a specified directory, retrieve specific configurations,
 * check if a configuration is loaded, and get a list of all configuration keys.
 *
 * <p>
 * Usage Example:
 * <pre>{@code
 * AlpinePlugin plugin = ; // your plugin instance
 * Class<MyConfig> configClass = MyConfig.class;
 * String rootDirectory = "configs";
 * ConfigLoader<MyConfig> configLoader = ConfigLoader.builder(plugin, configClass, rootDirectory)
 *            .addConfiguration("config1", MyConfig::new)
 *            .addConfiguration("config2", new MyConfig())
 *            .build();
 * }</pre>
 * </p>
 *
 * @param <T> The type of configuration class.
 * @since 0.4.0
 */
public final class ConfigLoader<T> {

    @Getter
    private final Path directory;

    private final Class<T> configClass;

    private final Map<String, T> configRegistry = new HashMap<>();

    private ConfigLoader(@NotNull AlpinePlugin plugin, @NotNull Class<T> configClass, @NotNull Path directory, @NotNull Map<String, Supplier<T>> defaultConfigs) {
        this.directory = directory;
        this.configClass = configClass;

        boolean createdDirectory = false;
        if (!Files.isDirectory(this.directory)) {
            try {
                Files.createDirectories(this.directory);
                createdDirectory = true;
            }
            catch (IOException ex) {
                throw new IllegalStateException("Unable to generate dynamic configuration root directory", ex);
            }
        }


        YamlConfigurationProperties properties = plugin.getConfigManager().properties;
        try (Stream<Path> stream = Files.list(this.directory)) {
            stream.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".yml")).forEach(file -> {
                T config = YamlConfigurations.load(file, this.configClass, properties);
                String fileName = file.getFileName().toString();
                String configName = fileName.substring(0, fileName.lastIndexOf("."));
                this.configRegistry.put(configName, config);
            });
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unable to load dynamic configuration", ex);
        }

        if (createdDirectory) {
            defaultConfigs.forEach((name, supplier) -> {
                T config = supplier.get();
                this.configRegistry.put(name, config);

                String fileName = name + ".yml";
                Path configPath = this.directory.resolve(fileName);
                YamlConfigurations.save(configPath, this.configClass, config, properties);
            });
        }
    }

    /**
     * Retrieves a set of all the configuration keys stored in the ConfigLoader object.
     *
     * @return A set containing all the configuration keys.
     */
    public @NotNull Set<String> getConfigKeys() {
        return this.configRegistry.keySet();
    }

    /**
     * Retrieves all the configurations stored in the ConfigLoader object.
     *
     * @return A collection containing all the configurations.
     */
    public @NotNull Collection<T> getConfigs() {
        return this.configRegistry.values();
    }

    /**
     * Determines if a configuration with the given name is loaded.
     *
     * @param name The name of the configuration to check.
     * @return true if a configuration with the given name is loaded, false otherwise.
     */
    public boolean isLoaded(@NotNull String name) {
        return this.configRegistry.containsKey(name);
    }

    /**
     * Retrieves the configuration object associated with the given name.
     *
     * @param name The name of the configuration to retrieve.
     * @return The configuration object associated with the name, or null if it doesn't exist.
     */
    public @Nullable T getConfig(@NotNull String name) {
        return this.configRegistry.get(name);
    }

    /**
     * Determines if the ConfigLoader is empty.
     *
     * @return true if the ConfigLoader is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.configRegistry.isEmpty();
    }

    public static <T> @NotNull Builder<T> builder(@NotNull AlpinePlugin plugin, @NotNull Class<T> configClass,
                                                  @NotNull String directory) {
        return new Builder<>(plugin, configClass, directory);
    }

    /**
     * @since 0.4.0
     */
    public static final class Builder<T> {


        private final AlpinePlugin plugin;

        private final Class<T> configClass;

        private final Path rootDirectory;

        private final Map<String, Supplier<T>> defaultConfigs = new HashMap<>();

        private Builder(@NotNull AlpinePlugin plugin, @NotNull Class<T> configClass, @NotNull String rootDirectory) {
            this.plugin = plugin;
            this.configClass = configClass;
            this.rootDirectory = plugin.getDataFolder().toPath().resolve(rootDirectory);
        }

        public @NotNull Builder<T> addConfiguration(@NotNull String name, @NotNull Supplier<T> configSupplier) {
            Validate.notNull(name, "name cannot be null");
            Validate.notNull(configSupplier, "configSupplier cannot be null");
            this.defaultConfigs.put(name, configSupplier);
            return this;
        }

        public @NotNull Builder<T> addConfiguration(@NotNull String name, @NotNull T config) {
            Validate.notNull(name, "name cannot be null");
            Validate.notNull(config, "config cannot be null");
            this.defaultConfigs.put(name, () -> config);
            return this;
        }

        public @NotNull ConfigLoader<T> build() {
            return new ConfigLoader<>(this.plugin, this.configClass, this.rootDirectory, this.defaultConfigs);
        }
    }
}
