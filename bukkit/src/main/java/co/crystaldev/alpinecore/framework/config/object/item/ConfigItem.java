/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.config.object.item;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.integration.PlaceholderIntegration;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Formatting;
import co.crystaldev.alpinecore.util.ItemHelper;
import com.cryptomorin.xseries.XItemFlag;
import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Polymorphic;
import de.exlll.configlib.PolymorphicTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a configurable item within the plugin framework.
 * Provides methods to build and manipulate item stacks based on dynamic configurations.
 *
 * @see DefinedConfigItem
 * @see VaryingConfigItem
 * @since 0.4.0
 */
@SuppressWarnings({"unchecked", "unused"})
@Polymorphic
@PolymorphicTypes({
        @PolymorphicTypes.Type(type = DefinedConfigItem.class),
        @PolymorphicTypes.Type(type = VaryingConfigItem.class)
})
public interface ConfigItem {

    @Nullable
    String getName();

    @Nullable
    List<String> getLore();

    boolean isEnchanted();

    int getCount();

    @Nullable
    Map<String, Object> getAttributes();

    /**
     * Retrieves the attribute associated with the given key.
     *
     * @param key  The key of the attribute
     * @param type The attribute type
     * @return The value associated with the key, or null if the key does not exist
     */
    default <T> @Nullable T getAttribute(@NotNull String key, @NotNull Class<T> type) {
        return this.getAttributes() == null ? null : type.cast(this.getAttributes().get(key));
    }

    /**
     * Retrieves the attribute associated with the given key.
     *
     * @param key The key of the attribute
     * @return The value associated with the key, or null if the key does not exist
     */
    default <T> @Nullable T getAttribute(@NotNull String key) {
        return this.getAttributes() == null ? null : (T) this.getAttributes().get(key);
    }

    /**
     * Checks if the given attribute key exists in the map of attributes.
     *
     * @param key The key of the attribute
     * @return true if the attribute key exists in the map of attributes, false otherwise
     */
    default boolean hasAttribute(@NotNull String key) {
        return this.getAttributes() != null && this.getAttributes().containsKey(key);
    }

    // region Material Type

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        if (type == null || !type.isSupported()) {
            type = XMaterial.AIR;
        }

        Material parsed = type.parseMaterial();
        count = Math.max(Math.min(parsed.getMaxStackSize(), count), 1);

        String replacedName = Formatting.placeholders(plugin, this.getName(), placeholders);

        PlaceholderIntegration integration = plugin.getActivatable(PlaceholderIntegration.class);
        if (integration != null) {
            replacedName = integration.replace(targetPlayer, otherPlayer, true, replacedName);
        }

        MiniMessage mm = plugin.getMiniMessage();
        Component name = Components.reset().append(mm.deserialize(replacedName));

        String joinedLore = this.getLore() == null || this.getLore().isEmpty() ? "" : String.join("\n", this.getLore());
        joinedLore = Formatting.placeholders(plugin, joinedLore, placeholders);
        if (integration != null) {
            joinedLore = integration.replace(targetPlayer, otherPlayer, true, joinedLore);
        }

        List<Component> lore = Stream.of(joinedLore.split("\n|<br>"))
                .map(v -> v.isEmpty() ? " " : v)
                .map(v -> Components.reset().append(mm.deserialize(v)))
                .collect(Collectors.toList());

        ItemStack stack = type.parseItem();
        stack.setAmount(count);

        ItemHelper.setDisplayName(stack, name);
        ItemHelper.setLore(stack, lore);

        if (this.isEnchanted()) {
            stack.addUnsafeEnchantment(Enchantment.LURE, 1);

            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(XItemFlag.HIDE_ENCHANTS.get());
            stack.setItemMeta(meta);
        }

        Map<String, Object> attributes = this.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            ConfigItemHelper.applyToItem(stack, attributes);
        }

        if (function != null) {
            stack = function.apply(stack);
        }

        return stack;
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, type, count, function, null, null, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
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
    default @NotNull ItemStack build(
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
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, type, this.getCount(), null, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            @Nullable Function<ItemStack, ItemStack> function,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, type, this.getCount(), function, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param count        The quantity of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            int count,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, type, count, null, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param type         The material type of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable XMaterial type,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, type, this.getCount(), null, targetPlayer, otherPlayer, placeholders);
    }

    // endregion Material Type

    // region ItemStack Type

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param stack        The item to extend
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull ItemStack stack,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        String replacedName = Formatting.placeholders(plugin, this.getName(), placeholders);

        PlaceholderIntegration integration = plugin.getActivatable(PlaceholderIntegration.class);
        if (integration != null) {
            replacedName = integration.replace(targetPlayer, otherPlayer, true, replacedName);
        }

        MiniMessage mm = plugin.getMiniMessage();
        Component name = Components.reset().append(mm.deserialize(replacedName));
        List<Component> lore = this.getLore() == null || this.getLore().isEmpty() ? Collections.emptyList() : this.getLore().stream()
                .map(v -> Formatting.placeholders(plugin, v, placeholders))
                .map(v -> integration == null ? v : integration.replace(targetPlayer, otherPlayer, true, v))
                .map(v -> Components.reset().append(mm.deserialize(v)))
                .collect(Collectors.toList());

        stack = stack.clone();
        stack.setAmount(Math.max(Math.min(stack.getMaxStackSize(), count), 1));

        ItemHelper.setDisplayName(stack, name);
        ItemHelper.setLore(stack, lore);

        if (this.isEnchanted()) {
            stack.addUnsafeEnchantment(Enchantment.LURE, 1);

            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(XItemFlag.HIDE_ENCHANTS.get());
            stack.setItemMeta(meta);
        }

        Map<String, Object> attributes = this.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            ConfigItemHelper.applyToItem(stack, attributes);
        }

        if (function != null) {
            stack = function.apply(stack);
        }

        return stack;
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param stack        The item to extend
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull ItemStack stack,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, stack, count, function, null, null, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param stack        The item to extend
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull ItemStack stack,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, stack, this.getCount(), function, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param stack        The item to extend
     * @param count        The quantity of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull ItemStack stack,
            int count,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, stack, count, null, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param stack        The item to extend
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull ItemStack stack,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, stack, this.getCount(), null, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param stack        The item to extend
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull ItemStack stack,
            @Nullable Function<ItemStack, ItemStack> function,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, stack, this.getCount(), function, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param stack        The item to extend
     * @param count        The quantity of the item
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull ItemStack stack,
            int count,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, stack, count, null, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param stack        The item to extend
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull ItemStack stack,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, stack, this.getCount(), null, targetPlayer, otherPlayer, placeholders);
    }

    // endregion ItemStack Type

    // region No Type

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * This method does not define a type by default.
     * It is for internal use within plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, (XMaterial) null, count, function, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * This method does not define a type by default.
     * It is for internal use within plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable Function<ItemStack, ItemStack> function,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, (XMaterial) null, this.getCount(), function, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * This method does not define a type by default.
     * It is for internal use within plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, (XMaterial) null, count, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * This method does not define a type by default.
     * It is for internal use within plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, (XMaterial) null, this.getCount(), placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     * <p>
     * This method does not define a type by default.
     * It is for internal use within plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @Nullable Function<ItemStack, ItemStack> function,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, (XMaterial) null, count, function, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     * <p>
     * This method does not define a type by default.
     * It is for internal use within plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param function     A function that can apply additional modifications to the ItemStack
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable Function<ItemStack, ItemStack> function,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, (XMaterial) null, this.getCount(), function, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     * <p>
     * This method does not define a type by default.
     * It is for internal use within plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param count        The quantity of the item
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            int count,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, (XMaterial) null, count, targetPlayer, otherPlayer, placeholders);
    }

    /**
     * Constructs an ItemStack based on the current configuration.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method
     * <p>
     * This method does not define a type by default.
     * It is for internal use within plugins (i.e., dynamic guis which define a type based on a state)
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param targetPlayer The target player
     * @param otherPlayer  The relational player
     * @param placeholders Optional placeholders for dynamic text replacement in item meta
     * @return A fully constructed and optionally modified ItemStack
     */
    default @NotNull ItemStack build(
            @NotNull AlpinePlugin plugin,
            @Nullable OfflinePlayer targetPlayer,
            @Nullable OfflinePlayer otherPlayer,
            @NotNull Object... placeholders
    ) {
        return this.build(plugin, (XMaterial) null, this.getCount(), targetPlayer, otherPlayer, placeholders);
    }

    // endregion No Type

    // region Give

    /**
     * Gives the constructed item to the specified player's inventory.
     * <p>
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
     * <p>
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
     * <p>
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
     * <p>
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

    // endregion Give
}
