/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a generic UI element.
 *
 * @since 0.4.0
 */
public final class GenericElement extends Element {

    private final ItemStack itemStack;

    public GenericElement(@NotNull UIContext context, @NotNull ItemStack itemStack) {
        super(context);
        this.itemStack = itemStack;
    }

    @Override
    public @Nullable ItemStack buildItemStack() {
        return this.itemStack;
    }
}
