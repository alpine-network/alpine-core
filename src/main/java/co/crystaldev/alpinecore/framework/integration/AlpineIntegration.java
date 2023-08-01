package co.crystaldev.alpinecore.framework.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

/**
 * A wrapper for {@link co.crystaldev.alpinecore.framework.engine.AlpineEngine} logic that will
 * only activate if a provided activation condition is satisfied.
 * <p>
 * Primarily used for allowing for easy soft-depending on other plugins.
 * <p>
 * Inheritors should never be manually instantiated.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineIntegration implements Listener, Activatable {
    /** The plugin that activated this engine */
    protected final AlpinePlugin plugin;

    /** The engine, if activated, that this integration controls */
    @Getter @Nullable
    private AlpineIntegrationEngine engine;

    /**
     * Locked down to prevent improper instantiation.
     * <p>
     * Integrations are reflectively instantiated by
     * the framework automatically.
     */
    protected AlpineIntegration(AlpinePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets the activation condition for the integration.
     *
     * @return Whether the integration should be activated
     */
    protected abstract boolean shouldActivate();

    /**
     * @return The class of the engine for this integration
     */
    @NotNull
    protected abstract Class<? extends AlpineIntegrationEngine> getEngineClass();

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        if (this.shouldActivate()) {
            try {
                Constructor<? extends AlpineIntegrationEngine> constructor = this.getEngineClass().getDeclaredConstructor(AlpinePlugin.class);
                constructor.setAccessible(true);
                AlpineIntegrationEngine engine = constructor.newInstance(this.plugin);
                this.plugin.getServer().getPluginManager().registerEvents(engine, this.plugin);
                this.engine = engine;
                this.plugin.log(String.format("&aIntegration activated &d%s", this.getClass().getSimpleName()));
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        HandlerList.unregisterAll(this);
        HandlerList.unregisterAll(this.engine);
        this.engine = null;
        this.plugin.log(String.format("&cIntegration deactivated &d%s", this.getClass().getSimpleName()));
    }

    @Override
    public final boolean isActive() {
        return this.engine != null;
    }

    @EventHandler
    public final void onPluginEnabled(PluginEnableEvent event) {
        // We schedule this 1 tick later as the plugin has not been removed from the internal registry yet
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, this::checkActivation, 1L);
    }

    @EventHandler
    public final void onPluginDisabled(PluginDisableEvent event) {
        // We schedule this 1 tick later as the plugin has not been removed from the internal registry yet
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, this::checkActivation, 1L);
    }

    /**
     * Accessible to allow plugins to utilize integrations
     * for purposes other than soft-depending on other plugins.
     * <p>
     * Do not call this unless you know what you're doing.
     */
    protected final void checkActivation() {
        boolean shouldActivate = this.shouldActivate();
        if (shouldActivate && !this.isActive()) {
            this.plugin.addActivatable(this);
        }
        else if (!shouldActivate && this.isActive()) {
            this.plugin.removeActivatable(this);
        }
    }
}
