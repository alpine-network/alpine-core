/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.event.type;

import co.crystaldev.alpinecore.framework.ui.event.UIEvent;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@Getter
public final class ClickEvent extends UIEvent {

    private final InventoryClickEvent handle;

    public ClickEvent(@NotNull InventoryClickEvent handle) {
        super();
        this.handle = handle;
    }
}
