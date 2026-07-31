/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.platform.Platform;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for creating inventories.
 *
 * @since 0.4.0
 */
public final class InventoryHelper {

    /**
     * Creates a new inventory with the specified parameters.
     *
     * @param holder the inventory holder
     * @param slots the number of slots in the inventory
     * @param title the title of the inventory
     * @return the created inventory
     */
    public static @NotNull Inventory createInventory(@NotNull InventoryHolder holder, int slots, @NotNull Component title) {
        return Platform.get().inventories().create(holder, slots, title);
    }

    /**
     * Creates a new inventory with the specified parameters.
     *
     * @param holder the inventory holder
     * @param type the type of the inventory
     * @param title the title of the inventory
     * @return the created inventory
     */
    public static @NotNull Inventory createInventory(@NotNull InventoryHolder holder, @NotNull InventoryType type, @NotNull Component title) {
        return Platform.get().inventories().create(holder, type, title);
    }
}
