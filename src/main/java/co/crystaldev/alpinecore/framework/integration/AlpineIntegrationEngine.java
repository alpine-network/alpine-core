package co.crystaldev.alpinecore.framework.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link co.crystaldev.alpinecore.framework.engine.AlpineEngine} that is controlled
 * by an {@link AlpineIntegration}.
 * <p>
 * Only active if the controlling integration's activation condition
 * is satisfied.
 *
 * @see AlpineIntegration
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineIntegrationEngine implements Listener {
    /** The plugin that activated this engine */
    protected final AlpinePlugin plugin;

    /**
     * Locked down to prevent improper instantiation.
     * <p>
     * Engines are reflectively instantiated by the
     * framework automatically.
     */
    protected AlpineIntegrationEngine(@NotNull AlpinePlugin plugin) {
        this.plugin = plugin;
    }
}
