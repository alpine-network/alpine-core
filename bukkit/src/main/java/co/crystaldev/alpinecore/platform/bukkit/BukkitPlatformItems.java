/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.bukkit;

import co.crystaldev.alpinecore.platform.PlatformItems;
import co.crystaldev.alpinecore.util.ReflectionHelper;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 0.5.0
 */
final class BukkitPlatformItems implements PlatformItems {

    // region Item meta

    private static final Method META_GET_DISPLAY_NAME = ReflectionHelper.findMethod(
            ItemMeta.class, "displayName");

    private static final Method META_SET_DISPLAY_NAME = ReflectionHelper.findMethod(
            ItemMeta.class, "displayName", Component.class);

    private static final Method META_GET_LORE = ReflectionHelper.findMethod(
            ItemMeta.class, "lore");

    private static final Method META_SET_LORE = ReflectionHelper.findMethod(
            ItemMeta.class, "lore", List.class);

    // endregion

    // region Enchantments

    private static final Method ENCHANTMENT_TRANSLATION_KEY = ReflectionHelper.findMethod(
            Enchantment.class, new String[]{ "translationKey", "getTranslationKey" });

    // endregion

    // region Potions

    private static final Method META_SET_BASE_POTION_TYPE = ReflectionHelper.findMethod(
            PotionMeta.class, "setBasePotionType", PotionType.class);

    private static final Class<?> POTION_DATA = ReflectionHelper.getClass(
            Bukkit.class.getClassLoader(), "org.bukkit.potion.PotionData");

    private static final Constructor<?> POTION_DATA_INIT = POTION_DATA == null ? null
            : ReflectionHelper.findConstructor(POTION_DATA, PotionType.class);

    private static final Method META_SET_BASE_POTION_DATA = POTION_DATA == null ? null
            : ReflectionHelper.findMethod(PotionMeta.class, "setBasePotionData", POTION_DATA);

    // endregion

    @Override
    public @Nullable Component displayName(@NotNull ItemMeta meta) {
        if (META_GET_DISPLAY_NAME != null) {
            Component name = ReflectionHelper.invokeMethod(Component.class, META_GET_DISPLAY_NAME, meta);
            if (name != null) {
                return name;
            }
        }

        String legacy = meta.getDisplayName();
        return legacy.isEmpty() ? null : BukkitComponentSerializer.legacy().deserialize(legacy);
    }

    @Override
    public void displayName(@NotNull ItemMeta meta, @NotNull Component name) {
        if (META_SET_DISPLAY_NAME != null) {
            ReflectionHelper.invokeMethod(META_SET_DISPLAY_NAME, meta, name);
        }
        else {
            meta.setDisplayName(BukkitComponentSerializer.legacy().serialize(name));
        }
    }

    @Override
    public @NotNull List<Component> lore(@NotNull ItemMeta meta) {
        if (META_GET_LORE != null) {
            List<Component> lore = ReflectionHelper.invokeMethod(META_GET_LORE, meta);
            if (lore != null) {
                return lore;
            }
        }

        List<String> legacy = meta.getLore();
        if (legacy == null) {
            return Collections.emptyList();
        }

        List<Component> lore = new ArrayList<>(legacy.size());
        for (String line : legacy) {
            lore.add(BukkitComponentSerializer.legacy().deserialize(line));
        }
        return lore;
    }

    @Override
    public void lore(@NotNull ItemMeta meta, @NotNull List<Component> lore) {
        if (META_SET_LORE != null) {
            ReflectionHelper.invokeMethod(META_SET_LORE, meta, lore);
        }
        else {
            List<String> legacy = new ArrayList<>(lore.size());
            for (Component line : lore) {
                legacy.add(BukkitComponentSerializer.legacy().serialize(line));
            }
            meta.setLore(legacy);
        }
    }

    @Override
    public @Nullable String translationKey(@NotNull Enchantment enchantment) {
        if (ENCHANTMENT_TRANSLATION_KEY == null) {
            return null;
        }
        return ReflectionHelper.invokeMethod(String.class, ENCHANTMENT_TRANSLATION_KEY, enchantment);
    }

    @Override
    public boolean basePotionType(@NotNull PotionMeta meta, @NotNull PotionType type) {
        // 1.20.5+
        if (META_SET_BASE_POTION_TYPE != null) {
            ReflectionHelper.invokeMethod(META_SET_BASE_POTION_TYPE, meta, type);
            return true;
        }

        // Older servers wrap the type in a PotionData.
        if (META_SET_BASE_POTION_DATA != null && POTION_DATA_INIT != null) {
            Object data = ReflectionHelper.invokeConstructor(POTION_DATA_INIT, type);
            if (data != null) {
                ReflectionHelper.invokeMethod(META_SET_BASE_POTION_DATA, meta, data);
                return true;
            }
        }

        return false;
    }
}
