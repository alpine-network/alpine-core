package co.crystaldev.alpinecore;

import co.aikar.commands.PaperCommandManager;
import co.crystaldev.alpinecore.framework.Activatable;
import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.ConfigManager;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.storage.KeySerializer;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import co.crystaldev.alpinecore.util.ChatColor;
import co.crystaldev.alpinecore.util.SimpleTimer;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * The base class for Alpine plugins. Use this
 * instead of {@link org.bukkit.plugin.java.JavaPlugin}.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class AlpinePlugin extends JavaPlugin implements Listener {
    /** Manages any {@link co.crystaldev.alpinecore.framework.config.AlpineConfig}s for the plugin */
    @Getter
    protected ConfigManager configManager;

    /** Manages any {@link co.crystaldev.alpinecore.framework.command.AlpineCommand}s for the plugin */
    @Getter
    protected PaperCommandManager commandManager;

    /** All {@link co.crystaldev.alpinecore.framework.Activatable}s registered for the plugin */
    @Getter
    private final Set<Activatable> activatables = new CopyOnWriteArraySet<>();

    /** Registry of config and key serializers for the plugin */
    @Getter
    private final SerializerRegistry serializerRegistry = new SerializerRegistry();

    // region Abstract methods
    /**
     * Called when the plugin is enabling.
     */
    public void onStart() {
        // NO-OP
    }

    /**
     * Called when the plugin is disabling.
     */
    public void onStop() {
        // NO-OP
    }

    /**
     * Registers custom serializers with the provided {@code SerializerRegistry}.
     * <br>
     * This method allows developers to register custom serializers for specific data types
     * with a central {@code SerializerRegistry} instance.
     *
     * @param serializerRegistry The {@code SerializerRegistry} where custom serializers should be registered.
     */
    public void registerSerializers(@NotNull SerializerRegistry serializerRegistry) {
        // key serializers
        serializerRegistry.putKeySerializer(Number.class, new KeySerializer.NumberKey());
        serializerRegistry.putKeySerializer(String.class, new KeySerializer.StringKey());
        serializerRegistry.putKeySerializer(UUID.class, new KeySerializer.UuidKey());
        serializerRegistry.putKeySerializer(OfflinePlayer.class, new KeySerializer.PlayerKey());

        // config serializers
        serializerRegistry.putConfigSerializer(ConfigMessage.class, new ConfigMessage.Serializer());
    }
    // endregion

    // region Override methods
    @Override
    public final void onEnable() {
        this.log("&e=== ENABLE START ===");
        // Start a timer
        SimpleTimer timer = new SimpleTimer();
        timer.start();

        // Setup and register custom data serializers
        this.registerSerializers(this.serializerRegistry);

        // Initialize the managers
        this.configManager = new ConfigManager(this, this.serializerRegistry);
        this.commandManager = new PaperCommandManager(this);

        // Setup ACF message config
        this.setupAcfLocale();

        // Activate all activatables
        this.activateAll();

        // Hand off to the plugin
        this.onStart();

        long startupTime = timer.stop();
        this.log(String.format("&e=== ENABLE &aCOMPLETE&e (&d%dms&e) ===", startupTime));
    }

    @Override
    public final void onDisable() {
        // Hand off to the plugin
        this.onStop();

        // Deactivate all activatables
        this.deactivateAll();
    }
    // endregion

    // region Unique methods
    /**
     * Retrieves a configuration instance from the manager.
     *
     * @param <T> The configuration type
     *
     * @param clazz The configuration class
     * @return The configuration instance
     */
    @NotNull
    public final <T extends AlpineConfig> T getConfiguration(@NotNull Class<T> clazz) {
        return this.configManager.getConfig(clazz);
    }

    /**
     * Logs an information message with color formatting.
     *
     * @param message The message to log
     */
    public final void log(@NotNull String message) {
        this.log(Level.INFO, message);
    }

    /**
     * Logs a severe message and error with color formatting.
     *
     * @param message   The message to log
     * @param throwable The error or exception
     */
    public final void log(@NotNull String message, @NotNull Throwable throwable) {
        this.getLogger().log(Level.SEVERE, ChatColor.translateToAnsi(message + "&r", true), throwable);
    }

    /**
     * Logs a message at a given level with color formatting.
     *
     * @param level   The log level
     * @param message The message to log
     */
    public final void log(@NotNull Level level, @NotNull String message) {
        this.getLogger().log(level, ChatColor.translateToAnsi(message + "&r", true));
    }

    /**
     * Registers an {@link co.crystaldev.alpinecore.framework.Activatable} to the plugin.
     *
     * @param activatable The activatable
     */
    public final void addActivatable(Activatable activatable) {
        activatable.activate(this);
        this.activatables.add(activatable);
    }

    /**
     * Unegisters an {@link co.crystaldev.alpinecore.framework.Activatable} from the plugin.
     *
     * @param activatable The activatable
     */
    public final void removeActivatable(Activatable activatable) {
        activatable.deactivate(this);
        this.activatables.remove(activatable);
    }

    /**
     * Locates all {@link co.crystaldev.alpinecore.framework.Activatable}s on the classpath in the
     * plugins package and activates them.
     */
    private void activateAll() {
        String packageName = this.getClass().getPackage().getName();
        Set<Class<?>> classes = ImmutableSet.of();
        try {
            classes = ClassPath.from(this.getClassLoader()).getAllClasses().stream()
                    .filter(clazz -> clazz.getPackageName().contains(packageName))
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toSet());
        }
        catch (Exception ex) {
            this.log("&cError scanning classpath", ex);
        }

        this.activate(classes, AlpineConfig.class::isAssignableFrom);
        this.activate(classes, AlpineIntegration.class::isAssignableFrom);
        this.activate(classes, AlpineEngine.class::isAssignableFrom);
        this.activate(classes, v -> true);
    }

    /**
     * Deactivates all {@link co.crystaldev.alpinecore.framework.Activatable}s.
     */
    private void deactivateAll() {
        for (Activatable activatable : this.activatables) {
            try {
                activatable.deactivate(this);
            }
            catch (Exception ex) {
                this.log(String.format("&cError deactivating &d%s", activatable.getClass().getSimpleName()), ex);
            }
        }
        this.activatables.clear();
    }

    private void activate(@NotNull Set<Class<?>> classes, @NotNull Predicate<Class<?>> classPredicate) {
        for (Class<?> clazz : new HashSet<>(classes)) {
            if (!Activatable.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers()))
                continue;

            if (!classPredicate.test(clazz))
                continue;

            try {
                if (AlpineConfig.class.isAssignableFrom(clazz)) {
                    // Configs need a no-args constructor due to a limitation imposed by ConfigLib
                    Constructor<? extends Activatable> constructor = ((Class<? extends Activatable>) clazz).getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Activatable activatable = constructor.newInstance();
                    activatable.activate(this);
                    this.activatables.add(activatable);
                }
                else {
                    Constructor<? extends Activatable> constructor = ((Class<? extends Activatable>) clazz).getDeclaredConstructor(AlpinePlugin.class);
                    constructor.setAccessible(true);
                    Activatable activatable = constructor.newInstance(this);
                    activatable.activate(this);
                    this.activatables.add(activatable);
                }
            }
            catch (Exception ex) {
                this.log(String.format("&cError activating &d%s", clazz.getSimpleName()), ex);
            }

            classes.remove(clazz);
        }
    }

    private void setupAcfLocale() {
        try {
            File acfConfig = new File(this.getDataFolder(), "acf_config.yml");
            InputStream is;

            // Save default to disk if not already there
            if (!acfConfig.exists()) {
                is = AlpineCore.getInstance().getResource("acf_config.yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8));
                config.save(acfConfig);
            }

            // Load to command manager
            this.commandManager.getLocales().loadYamlLanguageFile(acfConfig, this.commandManager.getLocales().getDefaultLocale());

            this.log("&aLoaded ACF message config");
        }
        catch (Exception ex) {
            this.log("&cError setting up ACF message config", ex);
        }
    }
    // endregion
}
