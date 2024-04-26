package co.crystaldev.alpinecore.framework.config.object.item;

import co.crystaldev.alpinecore.AlpinePlugin;
import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents a configurable item within the plugin framework.
 * Provides methods to build and manipulate item stacks based on dynamic configurations.
 *
 * @since 0.4.0
 */
@NoArgsConstructor @AllArgsConstructor @Getter
@Configuration
public class DefinedConfigItem implements ConfigItem {

    private XMaterial type;

    private String name;

    private List<String> lore;

    private boolean enchanted;

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

    @NotNull
    public static Builder builder(@NotNull XMaterial material) {
        return new Builder(material);
    }

    @RequiredArgsConstructor
    public static final class Builder {

        private final XMaterial material;

        private String name;
        private List<String> lore;
        private boolean enchanted;

        @NotNull
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        @NotNull
        public Builder lore(@NotNull String... lore) {
            List<String> processedLore = new LinkedList<>();
            for (String s : lore) {
                processedLore.addAll(Arrays.asList(s.split("(\n|<br>)")));
            }
            this.lore = processedLore;
            return this;
        }

        @NotNull
        public Builder enchanted() {
            this.enchanted = true;
            return this;
        }

        @NotNull
        public DefinedConfigItem build() {
            List<String> lore = this.lore == null ? Collections.emptyList() : this.lore ;
            return new DefinedConfigItem(this.material, this.name, lore, this.enchanted);
        }
    }
}