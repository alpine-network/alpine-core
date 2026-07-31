/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore;

import co.crystaldev.alpinecore.event.ServerTickEvent;
import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.alpinecore.framework.teleport.AlpineTeleportHandler;
import co.crystaldev.alpinecore.platform.Platform;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * The main class for the core plugin.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@ApiStatus.Internal
public final class AlpineCore extends AlpinePlugin {
    @Getter
    private static AlpineCore instance;
    {
        instance = this;
        Platform.bind();
    }

    static final AtomicLong TICK_COUNTER = new AtomicLong();

    private final Map<AlpinePlugin, Set<AlpineArgumentResolver<?>>> argumentResolvers = new HashMap<>();

    @Getter
    private InvalidUsageHandler<CommandSender> invalidCommandUsageHandler;

    @Override
    public void onStart() {
        ServerTickEvent event = new ServerTickEvent();
        scheduler().runTaskTimer(this, () -> {
            Bukkit.getPluginManager().callEvent(event);
            event.setTick(TICK_COUNTER.incrementAndGet());
        }, 0L, 1L);

        this.getTeleportManager().registerHandler(new AlpineTeleportHandler());
    }

    @Override
    public void onStop() {
        Platform.get().shutdown();
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() instanceof AlpinePlugin) {
            AlpinePlugin plugin = (AlpinePlugin) event.getPlugin();
            this.unregisterArgumentResolvers(plugin);
        }
    }

    public void registerArgumentResolver(@NotNull AlpinePlugin plugin, @NotNull AlpineArgumentResolver<?> resolver) {
        this.argumentResolvers.computeIfAbsent(plugin, k -> new HashSet<>()).add(resolver);
    }

    public void unregisterArgumentResolvers(@NotNull AlpinePlugin plugin) {
        this.argumentResolvers.remove(plugin);
    }

    public void forEachResolver(@NotNull Consumer<AlpineArgumentResolver> resolverConsumer) {
        this.argumentResolvers.forEach((plugin, resolvers) -> {
            resolvers.forEach(resolverConsumer);
        });
    }

    @Override
    public void setInvalidCommandUseHandler(@Nullable InvalidUsageHandler<CommandSender> handler) {
        this.invalidCommandUsageHandler = handler;
    }
}
