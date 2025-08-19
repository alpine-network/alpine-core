/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.type;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.ui.GuiType;
import co.crystaldev.alpinecore.framework.ui.handler.UIHandler;
import co.crystaldev.alpinecore.framework.ui.UIManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@Getter
public final class InventoryUI {

    private final ConfigInventoryUI properties;

    private final UIManager manager;

    private final UIHandler handler;

    private final GuiType type;

    public InventoryUI(@NotNull ConfigInventoryUI properties, @NotNull AlpinePlugin plugin, @NotNull UIHandler handler) {
        this.properties = properties;
        this.manager = plugin.getUiManager();
        this.handler = handler;
        this.type = GuiType.resolveType(properties.getSlots());
    }

    /**
     * Opens the inventory user interface for the specified player.
     *
     * @param player the player to open the inventory for
     * @param force  whether to override displayed uis
     */
    public void view(@NotNull Player player, boolean force) {
        this.manager.open(player, this, force);
    }

    /**
     * Opens the inventory user interface for the specified player.
     *
     * @param player the player to open the inventory for
     */
    public void view(@NotNull Player player) {
        this.manager.open(player, this);
    }

    /**
     * Swaps the user interface for a player with a new one.
     *
     * @param player the player whose user interface is being swapped
     */
    public void swap(@NotNull Player player) {
        this.manager.swap(player, this);
    }
}
