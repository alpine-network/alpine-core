package co.crystaldev.alpinecore.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegrationEngine;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    @Getter
    private static PlaceholderIntegration instance;
    { instance = this; }

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
     * @param target the command sender to be replaced against
     * @param text   the text to replace placeholders in
     * @return the modified text with placeholders replaced or the original text
     */
    public @NotNull String replace(@NotNull OfflinePlayer target, @NotNull String text) {
        if (this.isActive()) {
            return this.getEngine().replace(target, text);
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
    public @NotNull String replace(@NotNull OfflinePlayer target, @NotNull OfflinePlayer other, @NotNull String text) {
        if (this.isActive()) {
            return this.getEngine().replace(target, other, text);
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
    public @NotNull String replace(@NotNull CommandSender target, @NotNull String text) {
        if (this.isActive()) {
            return this.getEngine().replace(target, text);
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
    public @NotNull String replace(@NotNull CommandSender target, @NotNull CommandSender other, @NotNull String text) {
        if (this.isActive()) {
            return this.getEngine().replace(target, other, text);
        }
        else {
            return text;
        }
    }

    /**
     * @since 0.4.2
     */
    public static final class PlaceholderEngine extends AlpineIntegrationEngine {

        PlaceholderEngine(@NotNull AlpinePlugin plugin) {
            super(plugin);
        }

        public @NotNull String replace(@NotNull OfflinePlayer sender, @NotNull String text) {
            if (sender instanceof Player) {
                return PlaceholderAPI.setPlaceholders((Player) sender, text);
            }
            else {
                return PlaceholderAPI.setPlaceholders(sender, text);
            }
        }

        public @NotNull String replace(@NotNull OfflinePlayer sender, @NotNull OfflinePlayer target, @NotNull String text) {
            if (!(target instanceof Player)) {
                return this.replace(sender, text);
            }

            if (sender instanceof Player) {
                return PlaceholderAPI.setRelationalPlaceholders((Player) sender, (Player) target, text);
            }
            else {
                return PlaceholderAPI.setPlaceholders(sender, text);
            }
        }

        public @NotNull String replace(@NotNull CommandSender sender, @NotNull String text) {
            if (!(sender instanceof OfflinePlayer)) {
                return text;
            }

            return this.replace((OfflinePlayer) sender, text);
        }

        public @NotNull String replace(@NotNull CommandSender sender, @NotNull CommandSender target, @NotNull String text) {
            if (!(sender instanceof Player)) {
                return text;
            }

            if (!(target instanceof Player)) {
                return this.replace(sender, text);
            }

            return this.replace((OfflinePlayer) sender, (Player) target, text);
        }
    }
}
