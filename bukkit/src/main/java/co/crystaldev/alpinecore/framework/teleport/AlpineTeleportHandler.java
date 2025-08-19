/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.teleport;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@ApiStatus.Internal
public final class AlpineTeleportHandler implements TeleportHandler {

    @Override
    public void onInit(@NotNull TeleportContext context) {
        // NO OP
    }

    @Override
    public void onApply(@NotNull TeleportContext context) {
        // NO OP
    }

    @Override
    public void onCountdown(@NotNull TeleportContext context) {
        // NO OP
    }

    @Override
    public void onTeleport(@NotNull TeleportContext context) {
        Player player = context.player();
        if (player.isOnline()) {
            player.teleport(context.destination(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    @Override
    public void onMove(@NotNull TeleportContext context) {
        // NO OP
    }

    @Override
    public void onDamage(@NotNull TeleportContext context) {
        // NO OP
    }

    @Override
    public void onCancel(@NotNull TeleportContext context) {
        // NO OP
    }
}
