/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.bukkit;

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
import dev.rollczi.litecommands.extension.LiteExtension;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.5.0
 */
public final class BukkitPlatform implements AlpinePlatform {

    private static final boolean REGIONIZED = detectRegionized();

    private final BukkitPlatformAudiences audiences = new BukkitPlatformAudiences();

    private final BukkitPlatformItems items = new BukkitPlatformItems();

    private final BukkitPlatformInventories inventories = new BukkitPlatformInventories();

    @Override
    public @NotNull String id() {
        return "bukkit";
    }

    @Override
    public void initialize(@NotNull AlpinePlugin core) {
        this.audiences.initialize(core);
    }

    @Override
    public void shutdown() {
        this.audiences.close();
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
            try {
                @SuppressWarnings("unchecked")
                LiteExtension<CommandSender, ?> folia = (LiteExtension<CommandSender, ?>) Class
                        .forName("dev.rollczi.litecommands.folia.FoliaExtension")
                        .getDeclaredConstructor(Plugin.class)
                        .newInstance(plugin);
                builder.extension(folia);
            }
            catch (ReflectiveOperationException ex) {
                plugin.getLogger().warning("Failed to load Folia extension: " + ex.getMessage());
            }
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
