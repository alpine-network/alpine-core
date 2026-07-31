/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.paper;

import co.crystaldev.alpinecore.platform.PlatformInventories;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.5.0
 */
final class PaperPlatformInventories implements PlatformInventories {

    @Override
    public @NotNull Inventory create(@Nullable InventoryHolder holder, int slots, @NotNull Component title) {
        return Bukkit.createInventory(holder, slots, title);
    }

    @Override
    public @NotNull Inventory create(@Nullable InventoryHolder holder, @NotNull InventoryType type, @NotNull Component title) {
        return Bukkit.createInventory(holder, type, title);
    }

    @Override
    public @NotNull Inventory topInventory(@NotNull InventoryView view) {
        // invokeinterface - InventoryView became an interface in 1.21.
        return view.getTopInventory();
    }

    @Override
    public @NotNull Inventory topInventory(@NotNull HumanEntity viewer) {
        return viewer.getOpenInventory().getTopInventory();
    }
}
