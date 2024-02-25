package co.crystaldev.alpinecore.framework.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/24/2024
 */
public final class ServerTickEvent extends AlpineEvent {

    @Getter
    private final Server server = Bukkit.getServer();

    @Getter @Setter
    private long tick;

    public boolean isTime(long time, @NotNull TimeUnit unit) {
        return this.tick % (unit.toMillis(time) / 50) == 0;
    }
}
