package co.crystaldev.alpinecore.framework.config.object;

import co.crystaldev.alpinecore.util.Formatting;
import com.google.common.collect.ImmutableMap;
import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * A simple implementation of a random loot pool compatible with
 * {@link co.crystaldev.alpinecore.framework.config.AlpineConfig}.
 * <p>
 * Each draw will produce random a number between 0 and 100 for
 * each {@link Entry}. If that number is less than or equal to
 * it's chance, the reward is given.
 *
 * @see Entry
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@Configuration
@AllArgsConstructor @NoArgsConstructor
public class ConfigRewardPool {

    private List<Entry> entries;

    /**
     * Draw rewards for a given player.
     *
     * @param player The player
     */
    public void drawForPlayer(Player player) {
        Map<String, Object> placeholders = ImmutableMap.of("player", player.getName());
        this.drawInternal(placeholders, (s) -> {});
    }

    /**
     * Draw rewards for a given player with a callback for each granted reward.
     *
     * @param player The player
     * @param callback The callback
     */
    public void drawForPlayer(Player player, Consumer<String> callback) {
        Map<String, Object> placeholders = ImmutableMap.of("player", player.getName());
        this.drawInternal(placeholders, callback);
    }

    /**
     * Draw rewards for a given player with placeholders to format the commands with.
     *
     * @param player The player
     * @param placeholders The placeholders
     */
    public void drawForPlayer(Player player, Map<String, Object> placeholders) {
        placeholders.put("player", player.getName());
        this.drawInternal(placeholders, (s) -> {});
    }

    /**
     * Draw rewards for a given player with placeholders to format the commands with
     * and a callback for each granted reward.
     *
     * @param player The player
     * @param placeholders The placeholders
     * @param callback The callback
     */
    public void drawForPlayer(Player player, Map<String, Object> placeholders, Consumer<String> callback) {
        placeholders.put("player", player.getName());
        this.drawInternal(placeholders, callback);
    }

    private void drawInternal(Map<String, Object> placeholders, Consumer<String> callback) {
        for (Entry entry : this.entries) {
            double draw = ThreadLocalRandom.current().nextDouble(0.0D, 100.0D);
            if (entry.chance >= draw) {
                entry.executeCommands(placeholders);
                callback.accept(entry.name);
            }
        }
    }

    /**
     * Represents a possible reward.
     */
    @Configuration @NoArgsConstructor
    public static class Entry {
        private String name;
        private double chance;
        private List<String> commands;

        /**
         * @param name The display name
         * @param chance The chance of this reward being drawn
         * @param commands The commands to execute when this reward is drawn
         */
        public Entry(@NotNull String name, double chance, @NotNull List<String> commands) {
            Validate.isTrue(chance > 0.0D, "chance must be greater than 0");
            Validate.isTrue(chance < 100.1D, "chance must be less than 100");
            this.name = name;
            this.chance = chance;
            this.commands = commands;
        }

        protected void executeCommands(Map<String, Object> placeholders) {
            for (String command : this.commands) {
                command = Formatting.formatPlaceholders(command, placeholders);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }
}
