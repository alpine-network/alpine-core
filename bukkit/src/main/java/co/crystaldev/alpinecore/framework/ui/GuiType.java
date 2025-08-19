/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@AllArgsConstructor @Getter @ToString
public enum GuiType {
    CHEST(InventoryType.CHEST, 9, 1, 6),
    HOPPER(InventoryType.HOPPER, 5, 1, 1),
    DISPENSER(InventoryType.DISPENSER, 3, 3, 3);

    private final InventoryType inventoryType;

    private final int rowLength;

    private final int minHeight;

    private final int maxHeight;

    private boolean fits(String[] slots) {
        if (slots.length > this.maxHeight || slots.length < this.minHeight) {
            return false;
        }
        for (String row : slots) {
            if (row.length() > this.rowLength) {
                return false;
            }
        }
        return true;
    }

    private int waste(@NotNull String[] slots) {
        int excessRows = Math.max(0, this.getMaxHeight() - slots.length);
        int deficientRows = Math.max(0, slots.length - this.getMinHeight());
        return excessRows + deficientRows;
    }

    public static @NotNull GuiType resolveType(@NotNull String[] slots) {
        GuiType bestMatch = null;
        int minWaste = Integer.MAX_VALUE;

        for (GuiType type : values()) {
            if (type.fits(slots)) {
                int currentWaste = type.waste(slots);
                if (currentWaste < minWaste) {
                    minWaste = currentWaste;
                    bestMatch = type;
                }
            }
        }
        return bestMatch != null ? bestMatch : GuiType.CHEST;
    }

    public static @NotNull GuiType fromType(@NotNull InventoryType inventoryType) {
        for (GuiType type : values()) {
            if (type.inventoryType == inventoryType) {
                return type;
            }
        }
        return GuiType.CHEST;
    }
}
