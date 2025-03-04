package co.crystaldev.alpinecore;

import co.crystaldev.alpinecore.framework.Activatable;
import co.crystaldev.alpinecore.framework.Initializable;
import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.AlpinePluginConfig;
import co.crystaldev.alpinecore.framework.config.ConfigManager;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.storage.KeySerializer;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import co.crystaldev.alpinecore.framework.teleport.TeleportManager;
import co.crystaldev.alpinecore.framework.ui.UIManager;
import co.crystaldev.alpinecore.handler.InvalidCommandUsageHandler;
import co.crystaldev.alpinecore.integration.PlaceholderIntegration;
import co.crystaldev.alpinecore.integration.VaultIntegration;
import co.crystaldev.alpinecore.util.ChatColor;
import co.crystaldev.alpinecore.util.SimpleTimer;
import co.crystaldev.alpinecore.util.StyleTagResolver;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.adventure.bukkit.platform.LiteAdventurePlatformExtension;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * The base class for Alpine plugins. Use this
 * instead of {@link org.bukkit.plugin.java.JavaPlugin}.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@SuppressWarnings({"UnstableApiUsage", "unchecked", "unused"})
@Getter
public abstract class AlpinePlugin extends JavaPlugin implements Listener {

    /** Manages any {@link co.crystaldev.alpinecore.framework.config.AlpineConfig}s for the plugin */
    protected ConfigManager configManager;

    /** The configuration for the plugin */
    protected AlpinePluginConfig pluginConfig;

    /** Manages any {@link co.crystaldev.alpinecore.framework.command.AlpineCommand}s for the plugin */
    protected LiteCommands<CommandSender> commandManager;

    /** All {@link co.crystaldev.alpinecore.framework.Activatable}s registered for the plugin */
    private final Set<Activatable> activatables = new CopyOnWriteArraySet<>();

    /** Registry of config and key serializers for the plugin */
    private final SerializerRegistry serializerRegistry = new SerializerRegistry();

    /** Manager for handling inventory UIs. */
    private UIManager uiManager;

    /** Manager for handling deferred teleportation tasks */
    private TeleportManager teleportManager;

    /** MiniMessage curated by this plugin. */
    private MiniMessage miniMessage = MiniMessage.miniMessage();

    /** Strict MiniMessage curated by this plugin. */
    private MiniMessage strictMiniMessage = MiniMessage.builder().strict(true).build();

    // region Abstract methods

    /**
     * Called when the plugin is enabling.
     */
    public void onStart() {
        // NO OP
    }

    /**
     * Called when the plugin is disabling.
     */
    public void onStop() {
        // NO OP
    }

    /**
     * Internally invoked before loading an activatable object.
     * Determines whether an activatable object should be initialized.
     * By default, it allows all activatable objects to proceed with initialization.
     *
     * @param activatableClasspath The classpath of the activatable object.
     * @return boolean True to initialize the activatable object, false otherwise.
     */
    public boolean onActivatablePreload(@NotNull String activatableClasspath) {
        return true;
    }

    /**
     * Register custom serializers with the provided {@code SerializerRegistry}.
     * <br>
     * This method allows developers to register custom serializers for specific data types
     * with a central {@code SerializerRegistry} instance.
     *
     * @param serializerRegistry The {@code SerializerRegistry} where custom serializers should be registered.
     */
    public void registerSerializers(@NotNull SerializerRegistry serializerRegistry) {
        // NO OP
    }

    /**
     * Configures the command manager for the plugin with custom commands, settings, and message handlers.
     * This method sets up the {@link LiteCommandsBuilder} with the necessary configurations for command parsing.
     * It allows for further customization by invoking an overridable method that plugins can implement to modify
     * the command manager settings or add additional commands.
     *
     * @param builder the pre-configured {@link LiteCommandsBuilder} ready for plugin-specific configurations.
     * @see LiteCommandsBuilder
     * @see dev.rollczi.litecommands.bukkit.LiteBukkitFactory#builder(String)
     */
    public void setupCommandManager(@NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder) {
        // NO OP
    }

    /**
     * Configures default styles for text formatting within the plugin.
     * This method is used to add custom tags that modify the appearance of text.
     * <br>
     * It provides a straightforward way to enrich text presentation by associating
     * custom style tags with their corresponding formatting instructions.
     *
     * @param styleConsumer The consumer that accepts style definitions.
     */
    public void setupStyles(@NotNull StyleConsumer styleConsumer) {
        // NO OP
    }

    /**
     * Sets up default variables for use within the plugin.
     * <br>
     * Unlike styles, variables are placeholders that are not processed for formatting.
     * This method allows for the registration of default variables providing a simple
     * way to incorporate dynamic content.
     *
     * @param variableConsumer The consumer that accepts variable definitions.
     */
    public void setupVariables(@NotNull VariableConsumer variableConsumer) {
        // NO OP
    }

    /**
     * Configures the default configuration settings for this {@link AlpinePlugin}.
     * <br>
     * Implementations can override this method to apply custom configurations or adjust the existing ones.
     *
     * @param config the configuration.
     * @see AlpinePluginConfig
     */
    public void setupAlpineConfig(@NotNull AlpinePluginConfig config) {
        // NO OP
    }

    /**
     * Configures the MiniMessage parser with custom tag resolvers and other settings.
     * This method allows for customization of the MiniMessage parser.
     *
     * @param builder The MiniMessage {@link MiniMessage.Builder} instance to be configured with custom settings.
     * @return The MiniMessage instance.
     * @see MiniMessage
     */
    public @NotNull MiniMessage setupMiniMessage(@NotNull MiniMessage.Builder builder) {
        return builder.build();
    }

    /**
     * Retrieves the set of packages to be scanned for {@link Activatable} classes, configurations,
     * integrations, and other relevant plugin components.
     * <br>
     * This method allows developers to define the scope of class scanning for the plugin.
     * By default, it includes the package of the plugin's main class, but it can be extended
     * to include additional packages or domains as needed.
     *
     * @return A set of classes representing the base packages to scan.
     * @see Activatable
     * @see co.crystaldev.alpinecore.framework.config.AlpineConfig
     * @see co.crystaldev.alpinecore.framework.integration.AlpineIntegration
     */
    protected @NotNull Set<Class<?>> getScannablePackages() {
        return ImmutableSet.of(this.getClass());
    }

    // endregion

    // region Override methods

    @Override @ApiStatus.Internal
    public final void onEnable() {
        this.log("&e=== ENABLE START ===");
        // Start a timer
        SimpleTimer timer = new SimpleTimer();
        timer.start();

        // Setup and register custom data serializers
        this.serializerRegistry.putKeySerializer(Number.class, new KeySerializer.NumberKey());
        this.serializerRegistry.putKeySerializer(String.class, new KeySerializer.StringKey());
        this.serializerRegistry.putKeySerializer(UUID.class, new KeySerializer.UuidKey());
        this.serializerRegistry.putKeySerializer(OfflinePlayer.class, new KeySerializer.PlayerKey());
        this.serializerRegistry.putConfigSerializer(ConfigMessage.class, new ConfigMessage.Adapter());
        this.registerSerializers(this.serializerRegistry);

        // Initialize plugin managers
        this.configManager = new ConfigManager(this, this.serializerRegistry);
        this.uiManager = new UIManager(this);
        this.teleportManager = new TeleportManager(this);

        // Register plugin config
        this.setupAlpinePluginConfig();

        // Activate all activatables
        this.activateAll();

        // Setup plugin MiniMessage instances
        TagResolver resolver = TagResolver.resolver(TagResolver.standard(), new StyleTagResolver(this));
        this.miniMessage = this.setupMiniMessage(MiniMessage.builder().tags(resolver));
        this.strictMiniMessage = this.setupMiniMessage(MiniMessage.builder().tags(resolver).strict(true));

        // Initialize the command manager
        this.setupCommandManager();

        // Register this plugin with the EventBus
        this.getServer().getPluginManager().registerEvents(this, this);

        // Hand off to the plugin
        this.onStart();

        long startupTime = timer.stop();
        this.log(String.format("&e=== ENABLE &aCOMPLETE&e (&d%dms&e) ===", startupTime));
    }

    @Override @ApiStatus.Internal
    public final void onDisable() {
        // Hand off to the plugin
        this.onStop();

        // Deactivate all activatables
        this.deactivateAll();

        // Close all open guis
        this.uiManager.closeAll();

        // Unregister all commands from the server
        if (this.commandManager != null) {
            this.commandManager.unregister();
        }
    }

    // endregion

    // region Unique methods

    /**
     * Retrieves the configuration for this {@link AlpinePlugin}.
     *
     * @return The configuration.
     * @see AlpinePluginConfig
     */
    public final @NotNull AlpinePluginConfig getAlpineConfig() {
        return this.pluginConfig;
    }

    /**
     * Retrieves a configuration instance from the manager.
     *
     * @param <T> The configuration type
     *
     * @param clazz The configuration class
     * @return The configuration instance
     */
    public final <T extends AlpineConfig> @NotNull T getConfiguration(@NotNull Class<T> clazz) {
        if (AlpinePluginConfig.class.equals(clazz)) {
            return (T) this.getAlpineConfig();
        }

        return this.configManager.getConfig(clazz);
    }

    /**
     * Retrieves the current server tick.
     *
     * @return The current tick.
     */
    public long getCurrentTick() {
        return AlpineCore.TICK_COUNTER.get();
    }

    /**
     * Sets a global handler for managing invalid command usage.
     * <p>
     * This handler is shared globally across all {@link AlpinePlugin}
     * implementations and will override any previously set handler.
     *
     * @param handler The {@link InvalidUsageHandler} to handle invalid command usage.
     */
    public void setInvalidCommandUseHandler(@Nullable InvalidUsageHandler<CommandSender> handler) {
        AlpineCore.getInstance().setInvalidCommandUseHandler(handler);
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
        if (activatable.canDeactivate()) {
            activatable.deactivate(this);
        }
        this.activatables.remove(activatable);
    }

    /**
     * Retrieves an {@link co.crystaldev.alpinecore.framework.Activatable} instance of the specified class type from the plugin.
     *
     * @param clazz The type of activatable.
     * @return The activatable
     */
    public final <T extends Activatable> @Nullable T getActivatable(@NotNull Class<T> clazz) {
        for (Activatable activatable : this.activatables) {
            if (activatable.getClass().equals(clazz)) {
                return (T) activatable;
            }
        }
        return null;
    }

    /**
     * Locates all {@link co.crystaldev.alpinecore.framework.Activatable}s within the classpath and activates them.
     */
    private void activateAll() {
        Set<Class<?>> classes = new HashSet<>();
        try {
            for (Class<?> scannablePackage : this.getScannablePackages()) {
                String packageName = scannablePackage.getPackage().getName();
                ClassPath.from(scannablePackage.getClassLoader()).getAllClasses().stream()
                        .filter(clazz -> clazz.getPackageName().contains(packageName))
                        .filter(clazz -> this.onActivatablePreload(clazz.getName()))
                        .map(ClassPath.ClassInfo::load)
                        .forEach(classes::add);
            }

            classes.add(PlaceholderIntegration.class);
            classes.add(VaultIntegration.class);
        }
        catch (Exception ex) {
            this.log("&cError scanning classpath", ex);
        }

        this.activate(classes, c -> AlpineConfig.class.isAssignableFrom(c) && !AlpinePluginConfig.class.isAssignableFrom(c));
        this.activate(classes, AlpineIntegration.class::isAssignableFrom);
        this.activate(classes, AlpineEngine.class::isAssignableFrom);
        this.activate(classes, AlpineArgumentResolver.class::isAssignableFrom);
        this.activate(classes, AlpineCommand.class::isAssignableFrom);
        this.activate(classes, c ->  !AlpinePluginConfig.class.isAssignableFrom(c));
    }

    /**
     * Deactivates all {@link co.crystaldev.alpinecore.framework.Activatable}s.
     */
    private void deactivateAll() {
        for (Activatable activatable : this.activatables) {
            try {
                if (activatable.canDeactivate()) {
                    activatable.deactivate(this);
                }
            }
            catch (Exception ex) {
                this.log(String.format("&cError deactivating &d%s", activatable.getClass().getSimpleName()), ex);
            }
        }
        this.activatables.clear();
    }

    private void setupCommandManager() {
        AlpinePluginConfig messages = this.getAlpineConfig();
        AlpineCommand[] commands = this.activatables.stream()
                .filter(v -> v instanceof AlpineCommand)
                .filter(Activatable::isActive)
                .toArray(AlpineCommand[]::new);

        // Set up the LiteCommands builder
        LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder = LiteBukkitFactory.builder(this.getName())
                // <Required Arguments> [Optional Arguments]
                .schematicGenerator(SchematicFormat.angleBrackets())

                // Enable Adventure support
                .extension(new LiteAdventurePlatformExtension<>(BukkitAudiences.create(this)), config -> config
                        .miniMessage(true)
                        .legacyColor(true)
                        .colorizeArgument(true)
                        .serializer(this.miniMessage))

                // Use Bukkit permissions
                .settings(settings -> settings
                        .nativePermissions(true)
                        .fallbackPrefix(this.getName().toLowerCase(Locale.ROOT)))

                // Feed in our commands
                .commands((Object[]) commands)

                // Input our configurable messages
                .invalidUsage(new InvalidCommandUsageHandler(this))
                .message(LiteMessages.MISSING_PERMISSIONS, permission -> messages.missingPermissions.buildString(this, "permission", permission))
                .message(LiteMessages.INVALID_NUMBER, input -> messages.invalidNumber.buildString(this, "input", input))
                .message(LiteMessages.INSTANT_INVALID_FORMAT, input -> messages.invalidInstant.buildString(this, "input", input))
                .message(LiteBukkitMessages.WORLD_NOT_EXIST, input -> messages.invalidWorld.buildString(this, "input", input))
                .message(LiteBukkitMessages.LOCATION_INVALID_FORMAT, input -> messages.invalidLocation.buildString(this, "input", input))
                .message(LiteBukkitMessages.PLAYER_NOT_FOUND, input -> messages.playerNotFound.buildString(this, "player", input))
                .message(LiteBukkitMessages.PLAYER_ONLY, input -> messages.playerOnly.buildString(this));

        // Let the plugin mutate the command manager
        this.setupCommandManager(builder);

        // Register all argument resolvers
        AlpineCore.getInstance().forEachResolver(resolver -> {
            String key = resolver.getKey();
            if (key == null) {
                builder.argument(resolver.getType(), resolver);
            }
            else {
                builder.argument(resolver.getType(), ArgumentKey.of(key), resolver);
            }
        });

        // Let individual plugin commands mutate the command manager
        for (AlpineCommand command : commands) {
            command.setupCommandManager(builder);
        }

        // Build the command manager
        this.commandManager = builder.build();
    }

    private void activate(@NotNull Set<Class<?>> classes, @NotNull Predicate<Class<?>> classPredicate) {
        for (Class<?> clazz : new HashSet<>(classes)) {
            if (!Activatable.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers()))
                continue;

            if (!classPredicate.test(clazz)) {
                continue;
            }

            try {
                Activatable activatable;

                try {
                    Constructor<? extends Activatable> constructor = ((Class<? extends Activatable>) clazz).getDeclaredConstructor(AlpinePlugin.class);
                    constructor.setAccessible(true);
                    activatable = constructor.newInstance(this);
                }
                catch (NoSuchMethodException ex) {
                    Constructor<? extends Activatable> constructor = ((Class<? extends Activatable>) clazz).getDeclaredConstructor();
                    constructor.setAccessible(true);
                    activatable = constructor.newInstance();
                }

                // Initialize the activatable
                if (activatable instanceof Initializable) {
                    Initializable initializable = (Initializable) activatable;
                    if (initializable.init()) {
                        // Successfully initialized, activate
                        activatable.activate(this);
                        this.activatables.add(activatable);
                    }
                }
                else {
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

    private void setupAlpinePluginConfig() {
        // Initialize the config
        AlpinePluginConfig config = new AlpinePluginConfig();
        this.setupStyles((tag, style) -> config.styles.put(tag, style));
        this.setupVariables((name, variable) -> config.variables.put(name, variable));
        this.setupAlpineConfig(config);

        // Register with the config manager
        this.pluginConfig = this.configManager.loadConfig(config);

        // Override the config if defined
        if (this.pluginConfig.overrideWith != null && !this.pluginConfig.overrideWith.equals(this.getName())) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(this.pluginConfig.overrideWith);
            if (plugin instanceof AlpinePlugin) {
                AlpinePlugin other = (AlpinePlugin) plugin;
                this.log(String.format("&aReplacing AlpinePlugin config with &d%s", other.getName()));
                this.pluginConfig = other.pluginConfig;
            }
        }
    }

    @EventHandler
    private void onPluginLoad(PluginEnableEvent event) {
        if (!(event.getPlugin() instanceof AlpinePlugin)) {
            return;
        }

        AlpinePlugin other = (AlpinePlugin) event.getPlugin();
        if (other.getName().equalsIgnoreCase(this.pluginConfig.overrideWith)) {
            this.log(String.format("&aReplacing AlpinePlugin config with &d%s", other.getName()));
            this.pluginConfig = other.pluginConfig;
        }
    }

    // endregion

    @FunctionalInterface
    public interface StyleConsumer {
        void addStyle(@NotNull String tag, @NotNull String style);
    }

    @FunctionalInterface
    public interface VariableConsumer {
        void addVariable(@NotNull String name, @NotNull String variable);
    }
}
