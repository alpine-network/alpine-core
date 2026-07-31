/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.bukkit;

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
final class BukkitPlatformAudiences implements PlatformAudiences {

    @Override
    public @NotNull Audience sender(@NotNull CommandSender sender) {
        if (sender instanceof Audience) {
            return (Audience) sender;
        }
        return new BukkitSenderAudience(sender);
    }

    @Override
    public @NotNull Audience player(@NotNull Player player) {
        return this.sender(player);
    }

    @Override
    public @NotNull Audience all() {
        List<Audience> audiences = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            audiences.add(this.sender(player));
        }
        audiences.add(this.sender(Bukkit.getConsoleSender()));
        return Audience.audience(audiences);
    }
}
