package co.crystaldev.alpinecore;

import co.crystaldev.alpinecore.config.AlpineCoreConfig;
import co.crystaldev.alpinecore.framework.Activatable;
import co.crystaldev.alpinecore.framework.Initializable;
import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.ConfigManager;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.storage.KeySerializer;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import co.crystaldev.alpinecore.handler.CommandInvalidUsageHandler;
import co.crystaldev.alpinecore.util.ChatColor;
import co.crystaldev.alpinecore.util.SimpleTimer;
import co.crystaldev.alpinecore.util.StyleTagResolver;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.adventure.bukkit.platform.LiteAdventurePlatformExtension;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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
@SuppressWarnings({"UnstableApiUsage", "unchecked", "rawtypes", "unused"})
public abstract class AlpinePlugin extends JavaPlugin implements Listener {

    /** Manages any {@link co.crystaldev.alpinecore.framework.config.AlpineConfig}s for the plugin */
    @Getter
    protected ConfigManager configManager;

    /** Manages any {@link co.crystaldev.alpinecore.framework.command.AlpineCommand}s for the plugin */
    @Getter
    protected LiteCommands<CommandSender> commandManager;

    /** All {@link co.crystaldev.alpinecore.framework.Activatable}s registered for the plugin */
    @Getter
    private final Set<Activatable> activatables = new CopyOnWriteArraySet<>();

    /** Registry of config and key serializers for the plugin */
    @Getter
    private final SerializerRegistry serializerRegistry = new SerializerRegistry();

    /** MiniMessage curated by this plugin. */
    @Getter
    private MiniMessage miniMessage = MiniMessage.miniMessage();

    /** Strict MiniMessage curated by this plugin. */
    @Getter
    private MiniMessage strictMiniMessage = MiniMessage.builder().strict(true).build();

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
        // NO-OP
    }

    /**
     * Configures the command manager for the plugin with custom commands, settings, and message handlers.
     * This method sets up the {@link LiteCommandsBuilder} with the necessary configurations for command parsing.
     * It allows for further customization by invoking an overridable method that plugins can implement to modify
     * the command manager settings or add additional commands.
     *
     * @param builder the pre-configured {@link LiteCommandsBuilder} ready for plugin-specific configurations.
     * @see LiteCommandsBuilder
     * @see LiteCommandsBukkit#builder(String)
     */
    public void setupCommandManager(@NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder) {
        // NO-OP
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
    public void setupDefaultStyles(@NotNull StyleConsumer styleConsumer) {
        // NO-OP
    }

    /**
     * Configures the MiniMessage parser with custom tag resolvers and other settings.
     * This method allows for customization of the MiniMessage parser.
     *
     * @param builder The MiniMessage {@link MiniMessage.Builder} instance to be configured with custom settings.
     * @return The MiniMessage instance.
     * @see MiniMessage
     */
    @NotNull
    public MiniMessage setupMiniMessage(@NotNull MiniMessage.Builder builder) {
        return builder.build();
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
        this.serializerRegistry.putKeySerializer(Number.class, new KeySerializer.NumberKey());
        this.serializerRegistry.putKeySerializer(String.class, new KeySerializer.StringKey());
        this.serializerRegistry.putKeySerializer(UUID.class, new KeySerializer.UuidKey());
        this.serializerRegistry.putKeySerializer(OfflinePlayer.class, new KeySerializer.PlayerKey());
        this.serializerRegistry.putConfigSerializer(ConfigMessage.class, new ConfigMessage.Serializer());
        this.registerSerializers(this.serializerRegistry);

        // Initialize the config manager
        this.configManager = new ConfigManager(this, this.serializerRegistry);

        // Activate all activatables
        this.activateAll();

        // Register command config
        if (!this.configManager.isRegistered(AlpineCoreConfig.class)) {
            AlpineCoreConfig config = new AlpineCoreConfig();
            this.setupDefaultStyles((tag, style) -> config.styles.put(tag, style));

            this.configManager.registerConfig(config);
        }

        // Setup plugin MiniMessage instances
        TagResolver resolver = TagResolver.resolver(TagResolver.standard(), new StyleTagResolver(this));
        this.miniMessage = this.setupMiniMessage(MiniMessage.builder().tags(resolver));
        this.strictMiniMessage = this.setupMiniMessage(MiniMessage.builder().tags(resolver).strict(true));

        // Initialize the command manager
        this.setupCommandManager();

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

        // Unregister all commands from the server
        this.commandManager.unregister();
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
     * Retrieves the current server tick.
     *
     * @return The current tick.
     */
    public long getCurrentTick() {
        return AlpineCore.TICK_COUNTER.get();
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
     * Locates all {@link co.crystaldev.alpinecore.framework.Activatable}s on the classpath in the
     * plugins package and activates them.
     */
    private void activateAll() {
        String packageName = this.getClass().getPackage().getName();
        Set<Class<?>> classes = ImmutableSet.of();
        try {
            classes = ClassPath.from(this.getClassLoader()).getAllClasses().stream()
                    .filter(clazz -> clazz.getPackageName().contains(packageName))
                    .filter(clazz -> this.onActivatablePreload(clazz.getName()))
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toSet());
        }
        catch (Exception ex) {
            this.log("&cError scanning classpath", ex);
        }

        this.activate(classes, AlpineConfig.class::isAssignableFrom);
        this.activate(classes, AlpineIntegration.class::isAssignableFrom);
        this.activate(classes, AlpineEngine.class::isAssignableFrom);
        this.activate(classes, AlpineCommand.class::isAssignableFrom);
        this.activate(classes, AlpineArgumentResolver.class::isAssignableFrom);
        this.activate(classes, v -> true);
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
        AlpineCoreConfig messages = this.getConfigManager().getConfig(AlpineCoreConfig.class);
        AlpineCommand[] commands = this.activatables.stream()
                .filter(v -> v instanceof AlpineCommand)
                .filter(Activatable::isActive)
                .toArray(AlpineCommand[]::new);

        // Set up the LiteCommands builder
        LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder = LiteCommandsBukkit.builder(this.getName())
                // <Required Arguments> [Optional Arguments]
                .schematicGenerator(SchematicFormat.angleBrackets())

                // Enable Adventure support
                .extension(new LiteAdventurePlatformExtension<>(BukkitAudiences.create(this)), config -> config
                        .miniMessage(true)
                        .legacyColor(true)
                        .colorizeArgument(true)
                        .serializer(this.miniMessage))

                // Feed in our commands
                .commands((Object[]) commands)

                // Input our configurable messages
                .invalidUsage(new CommandInvalidUsageHandler(this))
                .message(LiteMessages.MISSING_PERMISSIONS, permission -> messages.missingPermissions.buildString(this, "permission", permission))
                .message(LiteMessages.INVALID_NUMBER, input -> messages.invalidNumber.buildString(this, "input", input))
                .message(LiteMessages.INSTANT_INVALID_FORMAT, input -> messages.invalidInstant.buildString(this, "input", input))
                .message(LiteBukkitMessages.WORLD_NOT_EXIST, input -> messages.invalidWorld.buildString(this, "input", input))
                .message(LiteBukkitMessages.LOCATION_INVALID_FORMAT, input -> messages.invalidLocation.buildString(this, "input", input))
                .message(LiteBukkitMessages.PLAYER_NOT_FOUND, input -> messages.playerNotFound.buildString(this, "player", input))
                .message(LiteBukkitMessages.PLAYER_ONLY, input -> messages.playerOnly.buildString(this));

        // Let the plugin mutate the command manager
        this.setupCommandManager(builder);

        // Register all activatable argument resolvers
        for (Activatable activatable : this.activatables) {
            if (!(activatable instanceof AlpineArgumentResolver<?>))
                continue;

            AlpineArgumentResolver resolver = (AlpineArgumentResolver) activatable;
            builder.argument(resolver.getType(), ArgumentKey.of(resolver.getKey()), resolver);
        }

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

            if (!classPredicate.test(clazz))
                continue;

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
    // endregion

    @FunctionalInterface
    public interface StyleConsumer {
        void addStyle(@NotNull String tag, @NotNull String style);
    }
}
