/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.scheduler.TaskScheduler;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.5.0
 */
public final class FakePlatform implements AlpinePlatform {

    @Override
    public @NotNull String id() {
        return "fake";
    }

    @Override
    public void initialize(@NotNull AlpinePlugin core) {
        // NO OP
    }

    @Override
    public void shutdown() {
        // NO OP
    }

    @Override
    public boolean isRegionized() {
        // NO OP
        return false;
    }

    @Override
    public @NotNull PlatformAudiences audiences() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull PlatformItems items() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull PlatformInventories inventories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull TaskScheduler createScheduler(@NotNull Server server) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void configureCommands(
            @NotNull AlpinePlugin plugin,
            @NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder,
            @NotNull MiniMessage serializer
    ) {
        throw new UnsupportedOperationException();
    }
}
