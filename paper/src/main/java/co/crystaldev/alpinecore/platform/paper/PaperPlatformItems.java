/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.paper;

import co.crystaldev.alpinecore.platform.PlatformItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @since 0.5.0
 */
final class PaperPlatformItems implements PlatformItems {

    @Override
    public @Nullable Component displayName(@NotNull ItemMeta meta) {
        return meta.displayName();
    }

    @Override
    public void displayName(@NotNull ItemMeta meta, @NotNull Component name) {
        meta.displayName(name);
    }

    @Override
    public @NotNull List<Component> lore(@NotNull ItemMeta meta) {
        List<Component> lore = meta.lore();
        return lore == null ? Collections.emptyList() : lore;
    }

    @Override
    public void lore(@NotNull ItemMeta meta, @NotNull List<Component> lore) {
        meta.lore(lore);
    }

    /**
     * {@code Enchantment#translationKey()} is deprecated for removal on Paper 26.2, so we're going to avoid it
     */
    @Override
    public @NotNull String translationKey(@NotNull Enchantment enchantment) {
        Component description = enchantment.description();
        if (description instanceof TranslatableComponent) {
            return ((TranslatableComponent) description).key();
        }

        NamespacedKey key = enchantment.getKey();
        return "enchantment." + key.getNamespace() + "." + key.getKey();
    }

    @Override
    public boolean basePotionType(@NotNull PotionMeta meta, @NotNull PotionType type) {
        meta.setBasePotionType(type);
        return true;
    }
}
