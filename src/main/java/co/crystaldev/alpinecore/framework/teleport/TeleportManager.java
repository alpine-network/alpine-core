package co.crystaldev.alpinecore.framework.teleport;

import co.crystaldev.alpinecore.AlpineCore;
import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Messaging;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * The TeleportManager class manages teleportation tasks and handlers.
 *
 * @since 0.4.0
 */
public final class TeleportManager {

    private final @NotNull AlpinePlugin plugin;

    private final Map<Player, TeleportTask> tasks = new ConcurrentHashMap<>();

    @ApiStatus.Internal
    public TeleportManager(@NotNull AlpinePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new TeleportListener(this, this.tasks), this.plugin);
    }

    /**
     * Retrieves the {@link TeleportHandler} registered with the teleport manager.
     *
     * @return the {@link TeleportHandler} registered with the teleport manager
     */
    public @NotNull TeleportHandler getTeleportHandler() {
        ServicesManager servicesManager = this.plugin.getServer().getServicesManager();
        RegisteredServiceProvider<TeleportHandler> registration = servicesManager.getRegistration(TeleportHandler.class);
        return registration.getProvider();
    }

    /**
     * Registers a {@link TeleportHandler} to handle the teleportation process.
     *
     * @param handler the {@link TeleportHandler} to register
     */
    public void registerHandler(@NotNull TeleportHandler handler) {
        Validate.notNull(handler, "handler cannot be null");

        ServicesManager servicesManager = this.plugin.getServer().getServicesManager();
        RegisteredServiceProvider<TeleportHandler> registration = servicesManager.getRegistration(TeleportHandler.class);
        if (registration != null && !(registration.getPlugin() instanceof AlpineCore)) {
            this.plugin.log(Level.WARNING, String.format("Replacing teleport handler \"%s\" with %s",
                    registration.getPlugin().getName(), this.plugin.getName()));
        }

        servicesManager.register(TeleportHandler.class, handler, this.plugin, ServicePriority.Highest);
    }

    /**
     * Initiates the teleportation process for the given task.
     *
     * @param task the teleportation task
     */
    public void initiateTeleport(@NotNull TeleportTask task) {
        TeleportHandler handler = this.getTeleportHandler();
        TeleportContext context = task.createContext(false);
        handler.onInit(context);
        task.getCallbacks().getOnInit().accept(context);

        if (!context.isCancelled()) {
            Messaging.send(context.player(), context.messageType(), context.message());
            TeleportTask oldTask = this.tasks.put(task.getPlayer(), task);
            if (oldTask != null) {
                cancelTask(oldTask, handler);
            }
        }
        else {
            cancelTask(task, handler);
        }
    }

    /**
     * Cancels the teleportation task for the given player.
     *
     * @param player the player whose teleportation task should be canceled
     */
    public void cancel(@NotNull Player player) {
        TeleportTask task = this.tasks.remove(player);
        if (task != null) {
            cancelTask(task, this.getTeleportHandler());
        }
    }

    private static void cancelTask(@NotNull TeleportTask task, @NotNull TeleportHandler handler) {
        TeleportContext context = task.createContext(true);
        handler.onCancel(context);
        task.getCallbacks().getOnCancel().accept(context);
        Messaging.send(context.player(), context.messageType(), context.message());
    }
}
