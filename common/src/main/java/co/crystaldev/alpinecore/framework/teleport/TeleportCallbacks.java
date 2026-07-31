/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.teleport;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * @since 0.4.0
 */
@Getter @Setter
@ApiStatus.Internal
final class TeleportCallbacks {
    private static final Consumer<TeleportContext> NO_OP = ctx -> {};
    private Consumer<TeleportContext> onInit = NO_OP;
    private Consumer<TeleportContext> onApply = NO_OP;
    private Consumer<TeleportContext> onDamage = NO_OP;
    private Consumer<TeleportContext> onMove = NO_OP;
    private Consumer<TeleportContext> onCountdown = NO_OP;
    private Consumer<TeleportContext> onTeleport = NO_OP;
    private Consumer<TeleportContext> onCancel = NO_OP;
}
