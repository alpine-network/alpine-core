/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.paper;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.scheduler.TaskScheduler;
import co.crystaldev.alpinecore.framework.scheduler.impl.BukkitTaskScheduler;
import co.crystaldev.alpinecore.framework.scheduler.impl.FoliaTaskScheduler;
import co.crystaldev.alpinecore.platform.AlpinePlatform;
import co.crystaldev.alpinecore.platform.PlatformAudiences;
import co.crystaldev.alpinecore.platform.PlatformInventories;
import co.crystaldev.alpinecore.platform.PlatformItems;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.folia.FoliaExtension;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.5.0
 */
public final class PaperPlatform implements AlpinePlatform {

    private static final boolean REGIONIZED = detectRegionized();

    private final PaperPlatformAudiences audiences = new PaperPlatformAudiences();

    private final PaperPlatformItems items = new PaperPlatformItems();

    private final PaperPlatformInventories inventories = new PaperPlatformInventories();

    @Override
    public @NotNull String id() {
        return "paper";
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
        return REGIONIZED;
    }

    @Override
    public @NotNull PlatformAudiences audiences() {
        return this.audiences;
    }

    @Override
    public @NotNull PlatformItems items() {
        return this.items;
    }

    @Override
    public @NotNull PlatformInventories inventories() {
        return this.inventories;
    }

    @Override
    public @NotNull TaskScheduler createScheduler(@NotNull Server server) {
        return REGIONIZED ? new FoliaTaskScheduler(server) : new BukkitTaskScheduler();
    }

    @Override
    public void configureCommands(
            @NotNull AlpinePlugin plugin,
            @NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder,
            @NotNull MiniMessage serializer
    ) {
        AlpinePlatform.super.configureCommands(plugin, builder, serializer);

        if (REGIONIZED) {
            builder.extension(new FoliaExtension(plugin));
        }
    }

    private static boolean detectRegionized() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        }
        catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
