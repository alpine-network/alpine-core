/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.interaction;

import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the context for a click event.
 *
 * @since 0.4.0
 */
public final class ClickContext extends InteractContext {

    private final ClickType type;

    private final InventoryAction action;

    private final ItemStack item;

    private boolean consumedItem;

    public ClickContext(@NotNull ClickType type, @NotNull InventoryAction action, @Nullable ItemStack item, @NotNull ActionResult result) {
        super(result);
        this.type = type;
        this.action = action;
        this.item = item;
    }

    /**
     * Retrieves the click type associated with this context.
     *
     * @return the click type
     */
    public @NotNull ClickType type() {
        return this.type;
    }

    /**
     * Retrieves the action associated with this context.
     *
     * @return the action
     */
    public @NotNull InventoryAction action() {
        return this.action;
    }

    /**
     * Checks if this context has an item.
     *
     * @return true if this context has an item, false otherwise
     */
    public boolean hasItem() {
        return this.item != null && this.item.getType() != Material.AIR && this.item.getAmount() > 0;
    }

    /**
     * Retrieves the item associated with this context.
     *
     * @return the item, or null if there is no item
     */
    public @Nullable ItemStack item() {
        return this.item;
    }

    /**
     * Consumes the item associated with this context.
     * <p>
     * This method removes the item from the cursor.
     *
     * @return the item, or null if there is no item
     */
    public @Nullable ItemStack consumeItem() {
        this.consumedItem = true;
        return this.item;
    }

    /**
     * Determines if the item associated with this context has been consumed.
     *
     * @return true if the item has been consumed, false otherwise
     */
    public boolean consumedItem() {
        return this.consumedItem;
    }
}
