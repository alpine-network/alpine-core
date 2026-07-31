/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.tomwmth.exampleplugin.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegrationEngine;
import co.crystaldev.alpinecore.util.Messaging;
import com.viaversion.viaversion.api.Via;
import dev.tomwmth.exampleplugin.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public class ViaIntegration extends AlpineIntegration {
    protected ViaIntegration(AlpinePlugin plugin) {
        super(plugin);
    }

    @Override
    protected boolean shouldActivate() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled("ViaVersion");
    }

    @Override
    protected @NotNull Class<? extends AlpineIntegrationEngine> getEngineClass() {
        return ViaEngine.class;
    }

    public static class ViaEngine extends AlpineIntegrationEngine {
        public ViaEngine(AlpinePlugin plugin) {
            super(plugin);
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Config config = this.plugin.getConfiguration(Config.class);

            int protocol = Via.getAPI().getPlayerVersion(event.getPlayer());

            Messaging.send(event.getPlayer(), config.integrationJoinMessage.build(this.plugin, "protocol", protocol));
        }
    }
}
