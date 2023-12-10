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
public class StatisticsStore extends AlpineStore<Player, Statistics> {
    @Getter
    private static StatisticsStore instance;
    {
        instance = this;
    }

    protected StatisticsStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<Player, Statistics>builder()
                .directory(new File(plugin.getDataFolder(), "/stats/"))
                .dataType(Statistics.class)
                .build());
    }
}
