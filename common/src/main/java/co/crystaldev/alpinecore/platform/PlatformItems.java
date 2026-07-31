/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform;

import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Platform-specific item and item-meta access.
 *
 * @since 0.5.0
 */
public interface PlatformItems {

    /**
     * @param meta the item meta, which the caller has already confirmed has a display name
     * @return the display name, or {@code null} if it could not be resolved
     */
    @Nullable Component displayName(@NotNull ItemMeta meta);

    /**
     * Sets the display name on the given meta.
     *
     * @param meta the item meta
     * @param name the display name
     */
    void displayName(@NotNull ItemMeta meta, @NotNull Component name);

    /**
     * @param meta the item meta, which the caller has already confirmed has lore
     * @return the lore, never {@code null} but possibly empty
     */
    @NotNull List<Component> lore(@NotNull ItemMeta meta);

    /**
     * Sets the lore on the given meta.
     *
     * @param meta the item meta
     * @param lore the lore
     */
    void lore(@NotNull ItemMeta meta, @NotNull List<Component> lore);

    /**
     * @param enchantment the enchantment
     * @return the vanilla translation key, or {@code null} if unavailable on this platform
     */
    @Nullable String translationKey(@NotNull Enchantment enchantment);

    /**
     * Applies a base potion type, bridging the 1.20.5 rename of
     * {@code setBasePotionData} to {@code setBasePotionType}.
     *
     * @param meta the potion meta
     * @param type the potion type
     * @return whether the type was applied
     */
    boolean basePotionType(@NotNull PotionMeta meta, @NotNull PotionType type);
}
