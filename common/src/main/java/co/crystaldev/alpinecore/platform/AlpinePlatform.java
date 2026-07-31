/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.scheduler.TaskScheduler;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The platform-specific half of AlpineCore.
 *
 * @since 0.5.0
 */
@ApiStatus.NonExtendable
public interface AlpinePlatform {

    /**
     * @return a stable identifier for this platform, for logging and diagnostics
     */
    @NotNull String id();

    /**
     * Invoked once, from AlpineCore's own enable, before any consumer plugin starts.
     *
     * @param core the AlpineCore plugin instance
     */
    void initialize(@NotNull AlpinePlugin core);

    /**
     * Invoked once, from AlpineCore's own disable. Must be idempotent.
     */
    void shutdown();

    /**
     * @return whether this server is region-threaded (Folia)
     */
    boolean isRegionized();

    /**
     * @return the audience provider
     */
    @NotNull PlatformAudiences audiences();

    /**
     * @return the item accessor
     */
    @NotNull PlatformItems items();

    /**
     * @return the inventory accessor
     */
    @NotNull PlatformInventories inventories();

    /**
     * Creates the process-wide task scheduler. Called at most once per JVM.
     *
     * @param server the server
     * @return the scheduler
     */
    @NotNull TaskScheduler createScheduler(@NotNull Server server);

    /**
     * Installs the LiteCommands extensions this platform needs.
     *
     * @param plugin     the plugin whose command manager is being built
     * @param builder    the builder
     * @param serializer the plugin's MiniMessage instance
     */
    default void configureCommands(
            @NotNull AlpinePlugin plugin,
            @NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder,
            @NotNull MiniMessage serializer
    ) {
        PlatformAudiences audiences = this.audiences();
        LiteAdventureExtension<CommandSender> extension = new LiteAdventureExtension<>(
                invocation -> audiences.sender(invocation.sender()));

        builder.extension(extension, config -> config
                .miniMessage(true)
                .legacyColor(true)
                .colorizeArgument(true)
                .serializer(serializer));
    }
}
