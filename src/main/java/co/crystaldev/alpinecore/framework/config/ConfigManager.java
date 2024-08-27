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

        if (!Files.exists(this.rootDirectory)) {
            try {
                Files.createDirectories(this.rootDirectory);
            }
            catch (IOException ex) {
                throw new IllegalStateException("Unable to generate configuration root directory", ex);
            }
        }

        YamlConfigurationProperties.Builder<?> builder = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .inputNulls(true)
                .outputNulls(true)
                .charset(StandardCharsets.UTF_8);

        serializerRegistry.getConfigSerializers().forEach((dataType, serializer) -> {
            builder.addSerializer((Class) dataType, serializer);
        });

        this.properties = builder.build();
    }

    public @NotNull AlpineConfig registerConfig(@NotNull AlpineConfig config) {
        Class<? extends AlpineConfig> clazz = config.getClass();
        if (this.registeredConfigurations.containsKey(clazz)) {
            throw new IllegalStateException("That type has already been registered as a configuration");
        }

        config = this.loadConfig(config);
        this.registeredConfigurations.put(clazz, config);
        return config;
    }

    public @NotNull AlpineConfig loadConfig(@NotNull AlpineConfig config) {
        Path file = this.rootDirectory.resolve(Paths.get(config.getFileName()));
        return YamlConfigurations.update(file, config.getClass(), this.properties);
    }

    public void unregisterConfig(@NotNull AlpineConfig config) {
        Class<? extends AlpineConfig> clazz = config.getClass();
        if (this.registeredConfigurations.containsKey(clazz)) {
            this.registeredConfigurations.remove(config);
        }
    }

    public boolean isRegistered(@NotNull AlpineConfig config) {
        Class<? extends AlpineConfig> clazz = config.getClass();
        return this.registeredConfigurations.containsKey(clazz);
    }

    public <T extends AlpineConfig> boolean isRegistered(@NotNull Class<T> clazz) {
        return this.registeredConfigurations.containsKey(clazz);
    }

    public <T extends AlpineConfig> @NotNull T getConfig(@NotNull Class<T> clazz) {
        AlpineConfig config = this.registeredConfigurations.get(clazz);
        if (config != null) {
            return (T) config;
        }
        else {
            throw new IllegalStateException("There was no configuration registered for that type");
        }
    }
}
