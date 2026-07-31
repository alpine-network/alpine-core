/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.bukkit;

import co.crystaldev.alpinecore.platform.PlatformInventories;
import co.crystaldev.alpinecore.util.ReflectionHelper;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * @since 0.5.0
 */
final class BukkitPlatformInventories implements PlatformInventories {

    private static final Method CREATE_CHEST = ReflectionHelper.findMethod(Bukkit.class,
            "createInventory", InventoryHolder.class, int.class, Component.class);

    private static final Method CREATE_TYPED = ReflectionHelper.findMethod(Bukkit.class,
            "createInventory", InventoryHolder.class, InventoryType.class, Component.class);

    @Override
    public @NotNull Inventory create(@Nullable InventoryHolder holder, int slots, @NotNull Component title) {
        if (CREATE_CHEST != null) {
            Inventory inventory = ReflectionHelper.invokeMethod(Inventory.class, CREATE_CHEST, null, holder, slots, title);
            if (inventory != null) {
                return inventory;
            }
        }

        return Bukkit.createInventory(holder, slots, legacy(title));
    }

    @Override
    public @NotNull Inventory create(@Nullable InventoryHolder holder, @NotNull InventoryType type, @NotNull Component title) {
        if (CREATE_TYPED != null) {
            Inventory inventory = ReflectionHelper.invokeMethod(Inventory.class, CREATE_TYPED, null,
                    holder, type, title);
            if (inventory != null) {
                return inventory;
            }
        }

        return Bukkit.createInventory(holder, type, legacy(title));
    }

    @Override
    public @NotNull Inventory topInventory(@NotNull InventoryView view) {
        // invokevirtual - correct against the legacy Bukkit floor, where InventoryView is a class.
        return view.getTopInventory();
    }

    @Override
    public @NotNull Inventory topInventory(@NotNull HumanEntity viewer) {
        return viewer.getOpenInventory().getTopInventory();
    }

    private static @NotNull String legacy(@NotNull Component title) {
        return BukkitComponentSerializer.legacy().serialize(title);
    }
}
