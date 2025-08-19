/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.ui.element.Element;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public final class EmptyElement extends Element {

    public static final EmptyElement INSTANCE = new EmptyElement();

    private static final ItemStack EMPTY_ITEM_STACK = new ItemStack(Material.AIR, 1);

    private EmptyElement() {
        super(null);
    }

    @Override
    public @NotNull ItemStack buildItemStack() {
        return EMPTY_ITEM_STACK;
    }
}
