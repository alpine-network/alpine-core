package co.crystaldev.alpinecore.framework.teleport;

import co.crystaldev.alpinecore.event.ServerTickEvent;
import co.crystaldev.alpinecore.util.Messaging;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

/**
 * @since 0.4.0
 */
@RequiredArgsConstructor
@ApiStatus.Internal
final class TeleportListener implements Listener {

    private final TeleportManager manager;

    private final Map<Player, TeleportTask> tasks;

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        if (this.tasks.isEmpty()) {
            return;
        }

        TeleportHandler handler = this.manager.getTeleportHandler();

        Iterator<Map.Entry<Player, TeleportTask>> iterator = this.tasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Player, TeleportTask> next = iterator.next();
            Player player = next.getKey();
            TeleportTask task = next.getValue();
            int ticksRemaining = task.tick();

            // ensure that the player is still in the same world
            if (!task.getOrigin().getWorld().equals(player.getWorld())) {
                this.manager.cancel(player);
                iterator.remove();
                continue;
            }

            TeleportContext context = null;
            boolean removed = false;
            if (ticksRemaining <= 0) {
                removed = true;
                iterator.remove();
                context = task.createContext(true);
                handler.onTeleport(context);
                task.getCallbacks().getOnTeleport().accept(context);
            }
            else if (ticksRemaining % 20 == 0) {
                context = task.createContext(false);
                handler.onCountdown(context);
                task.getCallbacks().getOnCountdown().accept(context);
            }

            if (context != null) {
                task.apply(context);

                Messaging.send(player, context.messageType(), context.message());

                if (context.isCancelled() && !removed) {
                    this.manager.cancel(context.player());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.tasks.containsKey(player)) {
            return;
        }

        TeleportHandler handler = this.manager.getTeleportHandler();
        TeleportTask task = this.tasks.get(player);
        TeleportContext context = task.createContext(!task.canMove());

        // do not listen to movement for instant teleportation
        if (task.getTicksToTeleport() < 0) {
            return;
        }

        // ensure that the player has even moved enough
        if (checkDistance(event.getTo(), task.getOrigin(), task.getMovementThreshold())) {
            return;
        }

        handler.onMove(context);
        task.getCallbacks().getOnMove().accept(context);
        task.apply(context);

        Messaging.send(context.player(), context.messageType(), context.message());

        if (context.isCancelled() || !task.canMove()) {
            this.manager.cancel(player);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        this.manager.cancel(event.getPlayer());
    }

    private static boolean checkDistance(@NotNull Location to, @NotNull Location from, double threshold) {
        double value;
        return Math.abs(from.getY() - to.getY()) < 1.25
                && (value = from.getX() - to.getX()) * value + (value = from.getZ() - to.getZ()) * value < threshold * threshold;
    }
}
