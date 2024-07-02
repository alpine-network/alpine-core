package co.crystaldev.alpinecore.event;

import co.crystaldev.alpinecore.framework.event.AlpineEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author BestBearr
 * @since 02/24/2024
 */
@Getter
public final class ServerTickEvent extends AlpineEvent {

    private final Server server = Bukkit.getServer();

    @Setter
    private long tick;

    public boolean isTime(long time, @NotNull TimeUnit unit) {
        return this.tick % (unit.toMillis(time) / 50) == 0;
    }
}
