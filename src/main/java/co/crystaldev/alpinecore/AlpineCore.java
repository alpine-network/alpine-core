package co.crystaldev.alpinecore;

import co.crystaldev.alpinecore.event.ServerTickEvent;
import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.alpinecore.framework.teleport.AlpineTeleportHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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
public final class AlpineCore extends AlpinePlugin implements Listener {
    // This is how we prefer singletons be created, but nothing will force you to do it like this
    @Getter
    private static AlpineCore instance;
    { instance = this; }

    static final AtomicLong TICK_COUNTER = new AtomicLong();

    private final Map<AlpinePlugin, Set<AlpineArgumentResolver<?>>> argumentResolvers = new HashMap<>();

    @Override
    public void onStart() {
        ServerTickEvent event = new ServerTickEvent();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getPluginManager().callEvent(event);
            event.setTick(TICK_COUNTER.incrementAndGet());
        }, 0L, 1L);
        Bukkit.getPluginManager().registerEvents(this, this);

        this.getTeleportManager().registerHandler(new AlpineTeleportHandler());
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
}
