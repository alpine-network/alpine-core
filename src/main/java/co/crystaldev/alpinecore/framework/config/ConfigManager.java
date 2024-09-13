package co.crystaldev.alpinecore.framework.config;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Manages instances of {@link AlpineConfig}s. One is
 * automatically created for each {@link AlpinePlugin}.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class ConfigManager {

    @Getter
    private final Path rootDirectory;
    private final Map<Class<? extends AlpineConfig>, AlpineConfig> registeredConfigurations = new HashMap<>();

    public final YamlConfigurationProperties properties;

    public ConfigManager(@NotNull AlpinePlugin plugin, @NotNull SerializerRegistry serializerRegistry) {
        this.rootDirectory = plugin.getDataFolder().toPath();

        // Ensure the root directory exists
        if (!Files.exists(this.rootDirectory)) {
            try {
                Files.createDirectories(this.rootDirectory);
            }
            catch (IOException ex) {
                throw new IllegalStateException("Unable to generate configuration root directory", ex);
            }
        }

        // Create the YamlConfiguration builder
        YamlConfigurationProperties.Builder<?> builder = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .inputNulls(true)
                .outputNulls(true)
                .charset(StandardCharsets.UTF_8);

        // Add serializers
        serializerRegistry.getConfigSerializers().forEach((dataType, serializer) -> {
            builder.addSerializer((Class) dataType, serializer);
        });

        this.properties = builder.build();
    }

    /**
     * Retrieves the configuration instance of the specified class from the registered configurations.
     *
     * @param clazz The class object of the configuration to retrieve.
     * @return The configuration instance of the specified class.
     * @throws IllegalStateException if there is no configuration registered for the specified type.
     */
    public <T extends AlpineConfig> @NotNull T getConfig(@NotNull Class<T> clazz) {
        AlpineConfig config = this.registeredConfigurations.get(clazz);
        if (config != null) {
            return (T) config;
        }
        else {
            throw new IllegalStateException("There was no configuration registered for that type");
        }
    }

    /**
     * Loads the specified configuration by resolving its file path and updating it using the properties.
     *
     * @param config the configuration object to be loaded
     * @return the updated configuration object
     */
    public <T extends AlpineConfig> @NotNull T loadConfig(@NotNull T config) {
        Path file = this.rootDirectory.resolve(Paths.get(config.getFileName()));
        Class<? extends AlpineConfig> clazz = config.getClass();
        return (T) YamlConfigurations.update(file, clazz, this.properties);
    }

    /**
     * Registers and loads the specified configuration object.
     *
     * @param config the configuration object to be registered and loaded
     * @return the registered and loaded configuration object
     */
    public <T extends AlpineConfig> @NotNull T registerConfig(@NotNull T config) {
        config = this.loadConfig(config);
        Class<? extends AlpineConfig> clazz = config.getClass();
        this.registeredConfigurations.put(clazz, config);
        return config;
    }

    /**
     * Edits the specified configuration object using the provided consumer.
     * The configuration is first registered and loaded, then passed to the consumer for modifications.
     *
     * @param <T> the type of the configuration object that extends AlpineConfig
     * @param config the configuration object to be edited
     * @param consumer the consumer that defines how to edit the configuration object
     * @return the edited configuration object
     */
    public <T extends AlpineConfig> @NotNull T editConfig(@NotNull T config, @NotNull Consumer<T> consumer) {
        Path file = this.rootDirectory.resolve(Paths.get(config.getFileName()));
        config = this.registerConfig(config);
        consumer.accept(config);
        YamlConfigurations.save(file, (Class<T>) config.getClass(), config, this.properties);
        return config;
    }

    /**
     * Unregisters and removes the specified configuration object.
     *
     * @param config the configuration object to be unregistered
     */
    public void unregisterConfig(@NotNull AlpineConfig config) {
        Class<? extends @NotNull AlpineConfig> clazz = config.getClass();
        this.registeredConfigurations.remove(clazz);
    }

    /**
     * Checks if the given configuration is registered.
     *
     * @param config the configuration object to check
     * @return true if the configuration is registered, false otherwise
     */
    public boolean isRegistered(@NotNull AlpineConfig config) {
        Class<? extends AlpineConfig> clazz = config.getClass();
        return this.registeredConfigurations.containsKey(clazz);
    }

    /**
     * Checks if the given configuration class is registered.
     *
     * @param clazz the class object of the configuration to check
     * @return true if the configuration class is registered, false otherwise
     */
    public <T extends AlpineConfig> boolean isRegistered(@NotNull Class<T> clazz) {
        return this.registeredConfigurations.containsKey(clazz);
    }
}
