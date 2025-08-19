/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.cooldown;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * @since 0.4.5
 */
@Getter @Setter
@ApiStatus.Internal
final class CooldownCallbacks<T> {
    private Consumer<Cooldown<T>> onInit = ctx -> {};
    private Consumer<Cooldown<T>> onMove = ctx -> {};
    private Consumer<Cooldown<T>> onCountdown = ctx -> {};
    private Consumer<Cooldown<T>> onComplete = ctx -> {};
    private Consumer<Cooldown<T>> onCancel = ctx -> {};
}
