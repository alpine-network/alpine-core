/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @since 0.4.0
 */
@RequiredArgsConstructor @Getter
final class UIState {

    private final Player player;

    private final Deque<UIContext> contextStack = new LinkedList<>();

    @Getter @Setter
    private boolean acceptInput = true;

    public @Nullable UIContext push(@NotNull UIContext context) {
        UIContext peek = this.peek();
        this.contextStack.push(context);
        return peek;
    }

    public @Nullable UIContext pop() {
        return this.contextStack.pop();
    }

    public @Nullable UIContext peek() {
        return this.contextStack.peek();
    }

    public boolean contains(@NotNull UIContext context) {
        return this.contextStack.contains(context);
    }

    public boolean isEmpty() {
        return this.contextStack.isEmpty();
    }
}
