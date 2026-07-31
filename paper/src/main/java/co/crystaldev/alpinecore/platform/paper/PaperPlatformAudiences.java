/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.paper;

import co.crystaldev.alpinecore.platform.PlatformAudiences;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 0.5.0
 */
final class PaperPlatformAudiences implements PlatformAudiences {

    @Override
    public @NotNull Audience sender(@NotNull CommandSender sender) {
        return sender;
    }

    @Override
    public @NotNull Audience player(@NotNull Player player) {
        return player;
    }

    @Override
    public @NotNull Audience console() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public @NotNull Audience all() {
        List<Audience> audiences = new ArrayList<>(Bukkit.getOnlinePlayers());
        audiences.add(Bukkit.getConsoleSender());
        return Audience.audience(audiences);
    }
}
