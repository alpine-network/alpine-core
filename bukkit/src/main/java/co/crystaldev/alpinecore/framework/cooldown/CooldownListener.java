package co.crystaldev.alpinecore.framework.cooldown;

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
 * @since 0.4.5
 */
@RequiredArgsConstructor
@ApiStatus.Internal
final class CooldownListener<T> implements Listener {

    private final CooldownHandler<T> handler;

    private final Map<T, Cooldown<T>> cooldowns;

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        if (this.cooldowns.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<T, Cooldown<T>>> iterator = this.cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<T, Cooldown<T>> next = iterator.next();
            Cooldown<T> cooldown = next.getValue();
            int ticksRemaining = cooldown.tick();

            boolean removed = false;
            if (ticksRemaining <= 0) {
                // cooldown has finished

                removed = true;
                iterator.remove();
                this.handler.getCallbacks().getOnComplete().accept(cooldown);

                cooldown.complete();
            }
            else if (ticksRemaining % 20 == 0) {
                // countdown the cooldown/warmup
                this.handler.getCallbacks().getOnCountdown().accept(cooldown);
            }

            // send the associated message
            T key = next.getKey();
            if (key instanceof Player) {
                Player player = (Player) key;
                Messaging.send(player, cooldown.messageType(), cooldown.message());
                cooldown.clearMessage();
            }

            // handle cancellation
            if (cooldown.isCancelled() && !removed) {
                this.handler.cancel(key);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.cooldowns.containsKey(player)) {
            return;
        }

        Cooldown<T> cooldown = this.cooldowns.get(player);

        // do not listen to movement for instant warmups
        if (!cooldown.isWarmup() || cooldown.remainingTicks() < 0) {
            return;
        }

        // ensure that the player has even moved enough
        if (checkDistance(event.getTo(), cooldown.getOrigin(), this.handler.getMovementThreshold())) {
            return;
        }

        this.handler.getCallbacks().getOnMove().accept(cooldown);

        Messaging.send(player, cooldown.messageType(), cooldown.message());

        if (cooldown.isCancelled() || !cooldown.canMove()) {
            this.handler.cancel((T) player);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.cooldowns.containsKey(player)) {
            this.handler.cancel((T) player);
        }
    }

    private static boolean checkDistance(@NotNull Location to, @NotNull Location from, double threshold) {
        double value;
        return Math.abs(from.getY() - to.getY()) < 1.25
                && (value = from.getX() - to.getX()) * value + (value = from.getZ() - to.getZ()) * value < threshold * threshold;
    }
}
