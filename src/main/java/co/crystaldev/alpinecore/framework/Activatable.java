package co.crystaldev.alpinecore.framework;

import co.crystaldev.alpinecore.AlpinePlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Forms the basis of the automatic activation
 * functionality for all systems.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public interface Activatable {
    /**
     * Activate this object.
     *
     * @param context The plugin which initiated the request
     */
    void activate(@NotNull AlpinePlugin context);

    /**
     * Deactivate this object.
     *
     * @param context The plugin which initiated the request
     */
    void deactivate(@NotNull AlpinePlugin context);

    /**
     * Is this object active?
     *
     * @return Whether the object is active
     */
    boolean isActive();
}
