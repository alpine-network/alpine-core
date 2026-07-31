/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.cooldown;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @since 0.4.5
 */
final class Warmup<T> extends Cooldown<T> {

    private final @NotNull Consumer<Cooldown<T>> onComplete;

    Warmup(@NotNull T entity, int remainingTicks, boolean canMove, @Nullable Location origin, @NotNull Consumer<Cooldown<T>> onComplete) {
        super(entity, remainingTicks, canMove, origin);
        this.onComplete = onComplete;
    }

    @Override
    void complete() {
        this.onComplete.accept(this);
    }
}
