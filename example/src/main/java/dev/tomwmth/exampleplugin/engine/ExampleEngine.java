package dev.tomwmth.exampleplugin.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.alpinecore.util.Components;
import dev.tomwmth.exampleplugin.config.Config;
import dev.tomwmth.exampleplugin.storage.Statistics;
import dev.tomwmth.exampleplugin.storage.StatisticsStore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Thomas Wearmouth <tomwmth@pm.me>
 * Created on 24/07/2023
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
