/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025-2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.tomwmth.exampleplugin.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import dev.tomwmth.exampleplugin.storage.Statistics;
import dev.tomwmth.exampleplugin.storage.StatisticsStore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public class ExampleEngine extends AlpineEngine {
    protected ExampleEngine(AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.incrementStats(event.getPlayer());
    }

    private void incrementStats(Player player) {
        StatisticsStore store = StatisticsStore.getInstance();
        Statistics stats = store.getOrCreate(player, new Statistics());
        stats.blocksBroken++;
        store.put(player, stats);
    }
}
