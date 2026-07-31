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
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Platform-specific inventory access.
 *
 * @since 0.5.0
 */
public interface PlatformInventories {

    /**
     * Creates a chest-style inventory with a {@link Component} title.
     *
     * @param holder the holder
     * @param slots  the slot count
     * @param title  the title
     * @return the inventory
     */
    @NotNull Inventory create(@Nullable InventoryHolder holder, int slots, @NotNull Component title);

    /**
     * Creates an inventory of the given type with a {@link Component} title.
     *
     * @param holder the holder
     * @param type   the inventory type
     * @param title  the title
     * @return the inventory
     */
    @NotNull Inventory create(@Nullable InventoryHolder holder, @NotNull InventoryType type, @NotNull Component title);

    /**
     * Equivalent to {@code view.getTopInventory()}.
     * <p>
     * This exists purely so the call site is compiled per platform.
     * {@link InventoryView} is an abstract class on the legacy Bukkit floor and an
     * <em>interface</em> since 1.21, so {@code javac} emits {@code invokevirtual} against the
     * former and {@code invokeinterface} against the latter. Getting that wrong is an
     * {@link IncompatibleClassChangeError} the first time a UI opens. Method descriptors are
     * indifferent to class-versus-interface, so passing the type across this boundary is safe.
     *
     * @param view the inventory view
     * @return the top inventory
     */
    @NotNull Inventory topInventory(@NotNull InventoryView view);

    /**
     * Equivalent to {@code viewer.getOpenInventory().getTopInventory()}.
     *
     * @param viewer the viewer
     * @return the top inventory of the viewer's open inventory
     */
    @NotNull Inventory topInventory(@NotNull HumanEntity viewer);
}
