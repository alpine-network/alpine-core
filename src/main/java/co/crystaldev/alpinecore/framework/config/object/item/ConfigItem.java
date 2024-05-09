package co.crystaldev.alpinecore.framework.config.object.item;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Formatting;
import co.crystaldev.alpinecore.util.ItemHelper;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a configurable item within the plugin framework.
 * Provides methods to build and manipulate item stacks based on dynamic configurations.
 *
 * @since 0.4.0
 */
public interface ConfigItem {

    @Nullable
    String getName();

    @Nullable
    List<String> getLore();

    boolean isEnchanted();

    int getCount();

    /**
     * Constructs an ItemStack based on the current configuration.
     * <br>
     * This method allows custom modifications via a function and supports placeholder replacement.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @NotNull
    default ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        if (type == null || !type.isSupported()) {
            type = XMaterial.AIR;
        }

        Material parsed = type.parseMaterial();
        count = Math.max(Math.min(parsed.getMaxStackSize(), count), 1);

        MiniMessage mm = plugin.getMiniMessage();
        Component name = Components.reset().append(mm.deserialize(Formatting.placeholders(plugin, this.getName(), placeholders)));
        List<Component> lore = this.getLore() == null || this.getLore().isEmpty() ? Collections.emptyList() : this.getLore().stream()
                .map(v -> Formatting.placeholders(v, placeholders))
                .map(v -> Components.reset().append(mm.deserialize(v)))
                .collect(Collectors.toList());

        ItemStack stack = new ItemStack(parsed, count);

        ItemHelper.setDisplayName(stack, name);
        ItemHelper.setLore(stack, lore);

        if (this.isEnchanted()) {
            stack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 0);

            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(meta);
        }

        if (function != null) {
            function.apply(stack);
        }

        return stack;
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <br>
     * This method allows custom modifications via a function and supports placeholder replacement.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @NotNull
    default ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, type, this.getCount(), function, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param count        The quantity of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @NotNull
    default ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            int count,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, type, count, null, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @NotNull
    default ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, type, this.getCount(), null, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <br>
     * This method does not define a type by default. It is for internal use within
     * plugins (i.e. dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @NotNull
    default ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, null, count, function, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <br>
     * This method does not define a type by default. It is for internal use within
     * plugins (i.e. dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @NotNull
    default ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, null, this.getCount(), function, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <br>
     * This method does not define a type by default. It is for internal use within
     * plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @NotNull
    default ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, null, count, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <br>
     * This method does not define a type by default. It is for internal use within
     * plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    @NotNull
    default ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, null, this.getCount(), placeholders);
    }

    /**
     * Gives the constructed item to the specified player's inventory.
     * <br>
     * This is a convenience method that builds the item and adds it directly to the player's inventory.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param player       The player who will receive the item
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     */
    default void give(
            @NotNull AlpinePlugin plugin,
            @NotNull XMaterial type,
            @NotNull Player player,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        ItemStack item = this.build(plugin, type, count, function, placeholders);
        player.getInventory().addItem(item);
    }

    /**
     * Gives the constructed item to the specified player's inventory.
     * <br>
     * This is a convenience method that builds the item and adds it directly to the player's inventory.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param player       The player who will receive the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     */
    default void give(
            @NotNull AlpinePlugin plugin,
            @NotNull XMaterial type,
            @NotNull Player player,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        ItemStack item = this.build(plugin, type, this.getCount(), function, placeholders);
        player.getInventory().addItem(item);
    }

    /**
     * Gives the constructed item to the specified player's inventory.
     * <br>
     * This is a convenience method that builds the item and adds it directly to the player's inventory.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param player       The player who will receive the item
     * @param count        The quantity of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     */
    default void give(
            @NotNull AlpinePlugin plugin,
            @NotNull XMaterial type,
            @NotNull Player player,
            int count,
            @NotNull Object... placeholders
    ) {
        this.give(plugin, type, player, count, null, placeholders);
    }

    /**
     * Gives the constructed item to the specified player's inventory.
     * <br>
     * This is a convenience method that builds the item and adds it directly to the player's inventory.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param player       The player who will receive the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     */
    default void give(
            @NotNull AlpinePlugin plugin,
            @NotNull XMaterial type,
            @NotNull Player player,
            @NotNull Object... placeholders
    ) {
        this.give(plugin, type, player, this.getCount(), null, placeholders);
    }

}
