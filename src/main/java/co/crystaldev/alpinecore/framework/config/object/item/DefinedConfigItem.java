package co.crystaldev.alpinecore.framework.config.object.item;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.ItemHelper;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Represents a configurable item within the plugin framework.
 * Provides methods to build and manipulate item stacks based on dynamic configurations.
 * </p>
 * Example usage:
 * <pre>{@code
 * Builder builder = DefinedConfigItem.builder(XMaterial.STICK);
 * DefinedConfigItem item = builder
 *      .name("<info>Defined Item")
 *      .lore(
 *          "These are some nifty lore lines!",
 *          "",
 *          "<info>  *</info> <emphasis>Type: %type%"
 *      )
 *      .count(64)
 *      .enchanted()
 *      .attribute("key", "value")
 *      .build();
 *
 * ItemStack builtItem = item.build(plugin,
 *      "type", item.getType());
 * }</pre>
 *
 * @since 0.4.0
 */
@NoArgsConstructor @AllArgsConstructor @Getter
@Configuration @SerializeWith(serializer = DefinedConfigItem.Adapter.class)
public class DefinedConfigItem implements ConfigItem {

    protected XMaterial type;

    protected String name;

    protected List<String> lore;

    protected int count = -1;

    protected boolean enchanted;

    protected Map<String, Object> attributes;

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @Override
    public @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, this.type, count, function, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @Override
    public @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, this.type, this.count, function, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @Override
    public @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, this.type, count, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @Override
    public @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, this.type, this.count, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @Override
    public @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, this.type, count, function, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @Override
    public @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable Function<ItemStack, ItemStack> function,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, this.type, this.count, function, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @Override
    public @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, this.type, count, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @Override
    public @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, this.type, this.count, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Gives the constructed item to the specified player's inventory.
     * <br>
     * This is a convenience method that builds the item and adds it directly to the player's inventory.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param player       The player who will receive the item
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     */
    public void give(
            @NotNull AlpinePlugin plugin,
            @NotNull Player player,
            int count,
            @Nullable Function<@NotNull ItemStack, @NotNull ItemStack> function,
            @NotNull Object... placeholders
    ) {
        this.give(plugin, this.type, player, count, function, placeholders);
    }

    /**
     * Gives the constructed item to the specified player's inventory.
     * <br>
     * This is a convenience method that builds the item and adds it directly to the player's inventory.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param player       The player who will receive the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     */
    public void give(
            @NotNull AlpinePlugin plugin,
            @NotNull Player player,
            @Nullable Function<@NotNull ItemStack, @NotNull ItemStack> function,
            @NotNull Object... placeholders
    ) {
        this.give(plugin, this.type, player, this.count, function, placeholders);
    }

    /**
     * Gives the constructed item to the specified player's inventory.
     * <br>
     * This is a convenience method that builds the item and adds it directly to the player's inventory.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param player       The player who will receive the item
     * @param count        The quantity of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     */
    public void give(
            @NotNull AlpinePlugin plugin,
            @NotNull Player player,
            int count,
            @NotNull Object... placeholders
    ) {
        this.give(plugin, this.type, player, count, placeholders);
    }

    /**
     * Gives the constructed item to the specified player's inventory.
     * <br>
     * This is a convenience method that builds the item and adds it directly to the player's inventory.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param player       The player who will receive the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     */
    public void give(
            @NotNull AlpinePlugin plugin,
            @NotNull Player player,
            @NotNull Object... placeholders
    ) {
        this.give(plugin, this.type, player, this.count, placeholders);
    }

    /**
     * Converts an ItemStack to a DefinedConfigItem.
     *
     * @param itemStack The ItemStack to convert
     * @return The converted DefinedConfigItem
     */
    public static @NotNull DefinedConfigItem fromItem(@NotNull ItemStack itemStack) {
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        return DefinedConfigItem.builder(XMaterial.matchXMaterial(itemStack))
                .name(serializer.serialize(ItemHelper.getDisplayName(itemStack)))
                .lore(ItemHelper.getJoinedLore(itemStack))
                .build();
    }

    public static @NotNull Builder builder(@NotNull XMaterial material) {
        return new Builder(material);
    }

    /**
     * @since 0.4.0
     */
    @RequiredArgsConstructor
    public static final class Builder {

        private final XMaterial material;

        private String name;
        private List<String> lore;
        private int count = -1;
        private boolean enchanted;
        private Map<String, Object> attributes;

        public @NotNull Builder name(@NotNull String name) {
            Validate.notNull(name, "name cannot be null");
            this.name = name;
            return this;
        }

        public @NotNull Builder lore(@NotNull String... lore) {
            Validate.notNull(lore, "lore cannot be null");
            List<String> processedLore = new LinkedList<>();
            for (String s : lore) {
                processedLore.addAll(Arrays.asList(s.split("(\n|<br>)")));
            }
            this.lore = processedLore;
            return this;
        }

        public @NotNull Builder lore(@NotNull Iterable<String> lore) {
            Validate.notNull(lore, "lore cannot be null");
            List<String> processedLore = new LinkedList<>();
            for (String s : lore) {
                processedLore.addAll(Arrays.asList(s.split("(\n|<br>)")));
            }
            this.lore = processedLore;
            return this;
        }

        public @NotNull Builder lore(@NotNull Component lore) {
            Validate.notNull(lore, "lore cannot be null");
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            String serialized = serializer.serialize(lore);
            this.lore = new LinkedList<>(Arrays.asList(serialized.split("(\n|<br>)")));
            return this;
        }

        public @NotNull Builder count(int count) {
            this.count = count;
            return this;
        }

        public @NotNull Builder enchanted() {
            this.enchanted = true;
            return this;
        }

        public @NotNull Builder enchant(@NotNull XEnchantment enchantment, int level) {
            return this.attribute("enchant_" + enchantment.name().toLowerCase(), level);
        }

        public @NotNull Builder potion(@NotNull XPotion effect, int duration, int amplifier) {
            return this.attribute("effect_" + effect.name().toLowerCase(), duration + " " + amplifier);
        }

        public @NotNull Builder primaryPotion(@NotNull XPotion effect) {
            return this.attribute("primary_effect", effect.name().toLowerCase());
        }

        public @NotNull Builder potionColor(@NotNull Color color) {
            return this.attribute("potion_color", color.asRGB());
        }

        public @NotNull Builder attribute(@NotNull String key, @Nullable Object value) {
            if (this.attributes == null) {
                if (value == null) {
                    return this;
                }

                this.attributes = new LinkedHashMap<>();
            }

            this.attributes.put(key, value);
            return this;
        }

        public @NotNull Builder attributes(@NotNull Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public @NotNull DefinedConfigItem build() {
            String name = this.name == null ? "" : this.name;
            List<String> lore = this.lore == null ? Collections.emptyList() : this.lore ;
            return new DefinedConfigItem(this.material, name, lore, this.count, this.enchanted, this.attributes);
        }
    }

    static final class Adapter extends ConfigItemAdapter {
        @Override
        public boolean requiresType() {
            return true;
        }
    }
}