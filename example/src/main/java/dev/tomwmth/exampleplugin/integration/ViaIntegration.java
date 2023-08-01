package dev.tomwmth.exampleplugin.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegrationEngine;
import co.crystaldev.alpinecore.util.Components;
import com.viaversion.viaversion.api.Via;
import dev.tomwmth.exampleplugin.config.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Wearmouth <tomwmth@pm.me>
 * Created on 26/07/2023
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
            Component prefix = config.prefix.build();

            int protocol = Via.getAPI().getPlayerVersion(event.getPlayer());

            Components.send(event.getPlayer(), prefix, config.integrationJoinMessage.build("protocol", protocol));
        }
    }
}
