/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.event;

import co.crystaldev.alpinecore.framework.event.AlpineEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author BestBearr
 * @since 0.3.0
 */
@Getter
public final class ServerTickEvent extends AlpineEvent {

    private final Server server = Bukkit.getServer();

    @Setter
    private long tick;

    public boolean isTime(long time, @NotNull TimeUnit unit) {
        return this.tick % (unit.toMillis(time) / 50) == 0;
    }
}
