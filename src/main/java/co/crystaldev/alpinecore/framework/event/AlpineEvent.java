package co.crystaldev.alpinecore.framework.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An {@link Event} implementation that handles the usual
 * {@link HandlerList} boilerplate.
 *
 * @see <a href="https://www.spigotmc.org/wiki/using-the-event-api/">Using the Event API</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public final HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
