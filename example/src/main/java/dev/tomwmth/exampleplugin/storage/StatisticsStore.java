/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.tomwmth.exampleplugin.storage;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class StatisticsStore extends AlpineStore<Player, Statistics> {
    @Getter
    private static StatisticsStore instance;
    { instance = this; }

    StatisticsStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<Player, Statistics>builder()
                .directory(new File(plugin.getDataFolder(), "/stats/"))
                .dataType(Statistics.class)
                .build());
    }
}
