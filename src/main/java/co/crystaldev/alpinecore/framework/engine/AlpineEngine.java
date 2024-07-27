package co.crystaldev.alpinecore.framework.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper around {@link Listener} to allow for
 * automatic activation by the framework.
 * <p>
 * Inheritors should never be manually instantiated.
 *
 * @see Listener
 * @see <a href="https://www.spigotmc.org/wiki/using-the-event-api/">Using the Event API</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineEngine implements Listener, Activatable {
    /** The plugin that activated this engine */
    protected final AlpinePlugin plugin;

    @Getter
    private boolean active = false;

    /**
     * Locked down to prevent improper instantiation.
     * <p>
     * Engines are reflectively instantiated by the
     * framework automatically.
     */
    protected AlpineEngine(@NotNull AlpinePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.active = true;
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        HandlerList.unregisterAll(this);
        this.active = false;
    }
}
