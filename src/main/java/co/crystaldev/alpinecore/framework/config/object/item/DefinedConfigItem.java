package co.crystaldev.alpinecore.framework.config.object.item;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.ItemHelper;
import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Represents a configurable item within the plugin framework.
 * Provides methods to build and manipulate item stacks based on dynamic configurations.
 *
 * @since 0.4.0
 */
@NoArgsConstructor @AllArgsConstructor @Getter
@Configuration @SerializeWith(serializer = ConfigItemAdapter.class)
public class DefinedConfigItem implements ConfigItem {

    protected XMaterial type;

    protected String name;

    protected List<String> lore;

    protected int count = -1;

    protected boolean enchanted;

    /**
     * Constructs an ItemStack based on the current configuration.
     * <br>
     * This method allows custom modifications via a function and supports placeholder replacement.
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
     * <br>
     * This method allows custom modifications via a function and supports placeholder replacement.
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
     * <br>
     * This method allows custom modifications via a function and supports placeholder replacement.
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
     * <br>
     * This method allows custom modifications via a function and supports placeholder replacement.
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
    @NotNull
    public static DefinedConfigItem fromItem(@NotNull ItemStack itemStack) {
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        return DefinedConfigItem.builder(XMaterial.matchXMaterial(itemStack))
                .name(serializer.serialize(ItemHelper.getDisplayName(itemStack)))
                .lore(ItemHelper.getJoinedLore(itemStack))
                .build();
    }

    @NotNull
    public static Builder builder(@NotNull XMaterial material) {
        return new Builder(material);
    }

    @RequiredArgsConstructor
    public static final class Builder {

        private final XMaterial material;

        private String name;
        private List<String> lore;
        private int count = -1;
        private boolean enchanted;

        @NotNull
        public Builder name(@NotNull String name) {
            Validate.notNull(name, "name cannot be null");
            this.name = name;
            return this;
        }

        @NotNull
        public Builder lore(@NotNull String... lore) {
            Validate.notNull(lore, "lore cannot be null");
            List<String> processedLore = new LinkedList<>();
            for (String s : lore) {
                processedLore.addAll(Arrays.asList(s.split("(\n|<br>)")));
            }
            this.lore = processedLore;
            return this;
        }

        @NotNull
        public Builder lore(@NotNull Iterable<String> lore) {
            Validate.notNull(lore, "lore cannot be null");
            List<String> processedLore = new LinkedList<>();
            for (String s : lore) {
                processedLore.addAll(Arrays.asList(s.split("(\n|<br>)")));
            }
            this.lore = processedLore;
            return this;
        }

        @NotNull
        public Builder lore(@NotNull Component lore) {
            Validate.notNull(lore, "lore cannot be null");
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            String serialized = serializer.serialize(lore);
            this.lore = new LinkedList<>(Arrays.asList(serialized.split("(\n|<br>)")));
            return this;
        }

        @NotNull
        public Builder count(int count) {
            this.count = count;
            return this;
        }

        @NotNull
        public Builder enchanted() {
            this.enchanted = true;
            return this;
        }

        @NotNull
        public DefinedConfigItem build() {
            String name = this.name == null ? "" : this.name;
            List<String> lore = this.lore == null ? Collections.emptyList() : this.lore ;
            return new DefinedConfigItem(this.material, name, lore, this.count, this.enchanted);
        }
    }
}