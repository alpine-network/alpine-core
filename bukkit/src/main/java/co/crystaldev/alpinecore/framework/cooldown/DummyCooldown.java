/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.cooldown;

import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.6
 */
final class DummyCooldown<T> extends Cooldown<T> {

    private static final Cooldown<?> INSTANCE = new DummyCooldown<>();

    DummyCooldown() {
        super(null, 0, false, null);
    }

    @Override
    public void setRemainingTicks(int remainingTicks) {
        // NO OP
    }

    public static <T> @NotNull Cooldown<T> instance() {
        INSTANCE.clearMessage();
        return (Cooldown<T>) INSTANCE;
    }
}
