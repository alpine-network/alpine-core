package co.crystaldev.alpinecore.framework.config;

import co.crystaldev.alpinecore.AlpinePlugin;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
    public static final YamlConfigurationProperties PROPERTIES = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .inputNulls(true).outputNulls(true)
            .build();

    @Getter
    private final File rootDirectory;
    private final Map<Class<? extends AlpineConfig>, AlpineConfig> registeredConfigurations = new HashMap<>();

    public ConfigManager(@NotNull AlpinePlugin plugin) {
        this.rootDirectory = plugin.getDataFolder();

        if (!this.rootDirectory.exists() && !this.rootDirectory.mkdirs()) {
            throw new IllegalStateException("Unable to generate configuration root directory");
        }
    }

    public void registerConfig(@NotNull AlpineConfig config) {
        Class<? extends AlpineConfig> clazz = config.getClass();
        if (!this.registeredConfigurations.containsKey(clazz)) {
            File configFile = new File(this.rootDirectory, config.getFileName());
            if (configFile.exists()) {
                AlpineConfig existingConfig = YamlConfigurations.load(configFile.toPath(), clazz, PROPERTIES);
                YamlConfigurations.save(configFile.toPath(), (Class<? super AlpineConfig>) clazz, config, PROPERTIES);
                this.registeredConfigurations.put(clazz, existingConfig);
            }
            else {
                YamlConfigurations.save(configFile.toPath(), (Class<? super AlpineConfig>) clazz, config, PROPERTIES);
                this.registeredConfigurations.put(clazz, config);
            }
        }
        else {
            throw new IllegalStateException("That type has already been registered as a configuration");
        }
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

    @NotNull
    public <T extends AlpineConfig> T getConfig(@NotNull Class<T> clazz) {
        AlpineConfig config = this.registeredConfigurations.get(clazz);
        if (config != null) {
            return (T) config;
        }
        else {
            throw new IllegalStateException("There was no configuration registered for that type");
        }
    }
}
