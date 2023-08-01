package co.crystaldev.alpinecore.framework.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * An {@link Event} implementation that handles the usual
 * {@link Cancellable} boilerplate.
 *
 * @see AlpineEvent
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineCancellableEvent extends AlpineEvent implements Cancellable {
    protected boolean cancelled = false;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
