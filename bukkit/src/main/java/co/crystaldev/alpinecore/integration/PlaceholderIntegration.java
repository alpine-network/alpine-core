package co.crystaldev.alpinecore.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegrationEngine;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A bundled integration of PlaceholderAPI
 * <p>
 * Allows AlpinePlugins to integrate PlaceholderAPI with
 * minimal code and without requiring its API to be added
 * as a dependency to their project.
 *
 * @since 0.4.2
 */
public final class PlaceholderIntegration extends AlpineIntegration {

    /**
     * Reflectively accessed dependency injection constructor.
     *
     * @param plugin Instance of the owner plugin
     */
    private PlaceholderIntegration(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @Override
    protected boolean shouldActivate() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @Override
    protected @NotNull Class<? extends AlpineIntegrationEngine> getEngineClass() {
        return PlaceholderEngine.class;
    }

    @Override
    public @NotNull PlaceholderEngine getEngine() {
        PlaceholderEngine engine = (PlaceholderEngine) super.getEngine();
        if (engine == null) {
            throw new IllegalStateException("PlaceholderAPI not installed");
        }
        return engine;
    }

    /**
     * Replaces placeholders in the given text if the integration is active. Otherwise, returns the original text.
     *
     * @param target      the command sender to be replaced against
     * @param text        the text to replace placeholders in
     * @param miniMessage Whether output chat colors should be serialized into {@link MiniMessage}
     * @return the modified text with placeholders replaced or the original text
     */
    public @NotNull String replace(@Nullable OfflinePlayer target, boolean miniMessage, @NotNull String text) {
        if (this.isActive()) {
            return this.getEngine().replace(target, text, miniMessage);
        }
        else {
            return text;
        }
    }

    /**
     * Replaces placeholders in the given text if the integration is active. Otherwise, returns the original text.
     *
     * @param target the command sender to be replaced against
     * @param other  the other command sender to be replaced against
     * @param text   the text to replace placeholders in
     * @return the modified text with placeholders replaced or the original text
     */
    public @NotNull String replace(@Nullable OfflinePlayer target, @Nullable OfflinePlayer other, boolean miniMessage,
                                   @NotNull String text) {
        if (this.isActive()) {
            return this.getEngine().replace(target, other, text, miniMessage);
        }
        else {
            return text;
        }
    }

    /**
     * Replaces placeholders in the given text if the integration is active. Otherwise, returns the original text.
     *
     * @param target the command sender to be replaced against
     * @param text   the text to replace placeholders in
     * @return the modified text with placeholders replaced or the original text
     */
    public @NotNull String replace(@Nullable CommandSender target, boolean miniMessage, @NotNull String text) {
        if (this.isActive()) {
            return this.getEngine().replace(target, text, miniMessage);
        }
        else {
            return text;
        }
    }

    /**
     * Replaces placeholders in the given text if the integration is active. Otherwise, returns the original text.
     *
     * @param target the command sender to be replaced against
     * @param other  the other command sender to be replaced against
     * @param text   the text to replace placeholders in
     * @return the modified text with placeholders replaced or the original text
     */
    public @NotNull String replace(@Nullable CommandSender target, @Nullable CommandSender other, boolean miniMessage,
                                   @NotNull String text) {
        if (this.isActive()) {
            return this.getEngine().replace(target, other, text, miniMessage);
        }
        else {
            return text;
        }
    }

    /**
     * @since 0.4.2
     */
    public static final class PlaceholderEngine extends AlpineIntegrationEngine {

        private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

        PlaceholderEngine(@NotNull AlpinePlugin plugin) {
            super(plugin);
        }

        public @NotNull String replace(@Nullable OfflinePlayer sender, @NotNull String text, boolean miniMessage) {
            if (sender == null) {
                return text;
            }

            if (sender instanceof Player) {
                return sanitize(this.plugin, miniMessage, text, v -> {
                    return PlaceholderAPI.setPlaceholders((Player) sender, v);
                });
            }
            else {
                return sanitize(this.plugin, miniMessage, text, v -> {
                    return PlaceholderAPI.setPlaceholders(sender, v);
                });
            }
        }

        public @NotNull String replace(@Nullable OfflinePlayer sender, @Nullable OfflinePlayer target,
                                       @NotNull String text, boolean miniMessage) {
            if (!(target instanceof Player)) {
                return this.replace(sender, text, miniMessage);
            }

            text = this.replace(sender, text, miniMessage);

            if (sender instanceof Player) {
                return sanitize(this.plugin, miniMessage, text, v -> {
                    return PlaceholderAPI.setRelationalPlaceholders((Player) sender, (Player) target, v);
                });
            }
            else {
                return sanitize(this.plugin, miniMessage, text, v -> {
                    return PlaceholderAPI.setPlaceholders(sender, v);
                });
            }
        }

        public @NotNull String replace(@Nullable CommandSender sender, @NotNull String text, boolean miniMessage) {
            if (!(sender instanceof OfflinePlayer)) {
                return text;
            }

            return this.replace((OfflinePlayer) sender, text, miniMessage);
        }

        public @NotNull String replace(@Nullable CommandSender sender, @Nullable CommandSender target,
                                       @NotNull String text, boolean miniMessage) {
            if (!(sender instanceof Player)) {
                return text;
            }

            if (!(target instanceof Player)) {
                return this.replace(sender, text, miniMessage);
            }

            return this.replace((OfflinePlayer) sender, (Player) target, text, miniMessage);
        }

        private static @NotNull String sanitize(@NotNull AlpinePlugin plugin, boolean serialize, @NotNull String text,
                                                @NotNull Function<String, String> transformer) {
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
            while (matcher.find()) {
                String placeholder = matcher.group();
                String replaced = transformer.apply(placeholder);

                if (replaced.trim().isEmpty()) {
                    text = text.replace(placeholder, "");
                    continue;
                }

                replaced = ChatColor.translateAlternateColorCodes('&', replaced);

                if (serialize && replaced.contains("§")) {
                    MiniMessage miniMessage = plugin.getStrictMiniMessage();
                    LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
                    replaced = miniMessage.serialize(serializer.deserialize(replaced));
                }

                text = text.replace(placeholder, replaced);
            }

            return text;
        }
    }
}
