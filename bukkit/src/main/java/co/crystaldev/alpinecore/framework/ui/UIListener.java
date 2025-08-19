/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui;

import co.crystaldev.alpinecore.event.ServerTickEvent;
import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import co.crystaldev.alpinecore.framework.ui.event.type.ClickEvent;
import co.crystaldev.alpinecore.framework.ui.event.type.DragEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * @since 0.4.0
 */
@RequiredArgsConstructor
final class UIListener implements Listener {

    private final UIManager manager;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (this.manager.isManaged(event.getInventory())) {
            this.manager.onClose(event.getInventory());
        }
    }

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        this.manager.onTick(event.getTick());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (!this.manager.isManaged(event.getInventory())) {
            return;
        }

        if (!this.manager.canInteract(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        UIContext context = this.manager.get(player.getUniqueId());
        context.setProcessingInput(true);

        ActionResult result = context.eventBus().call(context, new ClickEvent(event));
        if (result == ActionResult.CANCEL) {
            event.setCancelled(true);
        }

        context.setProcessingInput(false);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (!this.manager.isManaged(event.getInventory())) {
            return;
        }

        if (!this.manager.canInteract(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        UIContext context = this.manager.get(player.getUniqueId());
        context.setProcessingInput(true);

        ActionResult result = context.eventBus().call(context, new DragEvent(event));
        if (result == ActionResult.CANCEL) {
            event.setCancelled(true);
        }

        context.setProcessingInput(false);
    }
}
