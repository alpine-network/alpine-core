package co.crystaldev.alpinecore;

import co.crystaldev.alpinecore.framework.event.ServerTickEvent;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The main class for the core plugin.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class AlpineCore extends AlpinePlugin {
    // This is how we prefer singletons be created, but nothing will force you to do it like this
    @Getter
    private static AlpineCore instance;
    { instance = this; }

    private static final AtomicLong TICK_COUNTER = new AtomicLong();

    @Override
    public void onStart() {
        ServerTickEvent event = new ServerTickEvent();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getPluginManager().callEvent(event);
            event.setTick(TICK_COUNTER.incrementAndGet());
        }, 0L, 1L);
    }
}
