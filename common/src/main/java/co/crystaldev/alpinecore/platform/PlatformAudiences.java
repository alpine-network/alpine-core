/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Supplies {@link Audience}s for Bukkit senders.
 * <p>
 * On platforms with native Adventure support the sender <em>is</em> an {@link Audience} and
 * implementations return it directly. On platforms without, an implementation bridges the gap.
 * <p>
 * The method names intentionally mirror the now-archived {@code BukkitAudiences} so existing
 * call sites keep compiling.
 *
 * @since 0.5.0
 */
public interface PlatformAudiences {

    /**
     * Wraps any command sender.
     *
     * @param sender the sender
     * @return the audience
     */
    @NotNull Audience sender(@NotNull CommandSender sender);

    /**
     * Wraps a player.
     *
     * @param player the player
     * @return the audience
     */
    @NotNull Audience player(@NotNull Player player);

    /**
     * @return an audience for the server console
     */
    default @NotNull Audience console() {
        return this.sender(Bukkit.getConsoleSender());
    }

    /**
     * @return an audience covering every online player and the console
     */
    @NotNull Audience all();

    /**
     * Releases any resources held by this provider. Must be idempotent, and is a no-op on
     * platforms that need no bridge.
     */
    default void close() {
    }
}
