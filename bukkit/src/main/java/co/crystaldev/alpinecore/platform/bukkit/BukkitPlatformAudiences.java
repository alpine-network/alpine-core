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
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 0.5.0
 */
final class BukkitPlatformAudiences implements PlatformAudiences {

    private final Object lock = new Object();

    private Plugin plugin;

    private volatile BukkitAudiences provider;

    /** Called from {@link BukkitPlatform#initialize}, before any consumer plugin starts. */
    void initialize(@NotNull Plugin plugin) {
        synchronized (this.lock) {
            this.plugin = plugin;
        }
    }

    @Override
    public @NotNull Audience sender(@NotNull CommandSender sender) {
        if (sender instanceof Audience) {
            return (Audience) sender;
        }
        return this.provider().sender(sender);
    }

    @Override
    public @NotNull Audience player(@NotNull Player player) {
        if (player instanceof Audience) {
            return (Audience) player;
        }
        return this.provider().player(player);
    }

    @Override
    public @NotNull Audience all() {
        List<Audience> audiences = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            audiences.add(this.player(player));
        }
        audiences.add(this.sender(Bukkit.getConsoleSender()));
        return Audience.audience(audiences);
    }

    @Override
    public void close() {
        synchronized (this.lock) {
            if (this.provider != null) {
                this.provider.close();
                this.provider = null;
            }
        }
    }

    private @NotNull BukkitAudiences provider() {
        BukkitAudiences current = this.provider;
        if (current != null) {
            return current;
        }

        synchronized (this.lock) {
            if (this.provider == null) {
                if (this.plugin == null) {
                    throw new IllegalStateException("AlpineCore's platform is not initialized yet; "
                            + "audiences are unavailable until AlpineCore enables");
                }
                this.provider = BukkitAudiences.create(this.plugin);
            }
            return this.provider;
        }
    }
}
