package co.crystaldev.alpinecore.framework.cooldown;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Messaging;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A handler to manage cooldowns or warmups for given entities.
 * </p>
 * Example usage:
 * <pre>{@code
 * AlpinePlugin plugin;
 * CooldownHandler<Player> handler = CooldownHandler.<Player>builder()
 *         .delay(5, TimeUnit.SECONDS)
 *         .build(plugin);
 *
 * public void onEvent(Player player) {
 *     Cooldown<Player> cooldown = handler.testCooldown(player);
 *     if (cooldown.isActive()) {
 *         player.sendMessage("You are on cooldown for " + cooldown.remainingTime(TimeUnit.SECONDS));
 *     }
 *     else {
 *         // Cooldown is not active
 *     }
 * }
 * }</pre>
 *
 * @since 0.4.5
 */
public final class CooldownHandler<T> {

    private final int delayTicks;

    @Getter
    private final double movementThreshold;

    @Getter
    private final CooldownCallbacks<T> callbacks;

    private final Map<T, Cooldown<T>> cooldowns = new ConcurrentHashMap<>();

    private CooldownHandler(@NotNull AlpinePlugin plugin, int delayTicks, double movementThreshold, @NotNull CooldownCallbacks<T> callbacks) {
        this.delayTicks = delayTicks;
        this.movementThreshold = movementThreshold;
        this.callbacks = callbacks;

        Bukkit.getPluginManager().registerEvents(new CooldownListener<>(this, this.cooldowns), plugin);
    }

    /**
     * Checks or initializes a cooldown for the given key.
     *
     * @param key the key representing the entity to be checked or initialized for cooldown
     * @return the current cooldown associated with the key
     */
    public @NotNull Cooldown<T> testCooldown(@NotNull T key) {
        Location origin = key instanceof Player ? ((Player) key).getLocation() : null;
        Cooldown<T> cooldown = this.cooldowns.getOrDefault(key, DummyCooldown.instance());

        // Cooldown is not in effect, assign a new cooldown
        if (!cooldown.isActive()) {
            Cooldown<T> newCooldown = new Cooldown<>(key, this.delayTicks, this.movementThreshold < 0, origin);
            this.cooldowns.put(key, newCooldown);
            this.callbacks.getOnInit().accept(newCooldown);

            if (key instanceof Player) {
                Messaging.send((Player) key, cooldown.messageType(), cooldown.message());
            }

            if (newCooldown.isCancelled()) {
                this.cancel(key);
            }
        }

        return cooldown;
    }

    /**
     * Tests or initializes the warmup for the given key.
     * If the warmup has not been initialized, it begins the warmup process.
     *
     * @param key        the key representing the entity to be checked or initialized for warmup
     * @param onComplete the callback to be executed upon the completion of the warmup
     * @return the current warmup associated with the key
     */
    public @NotNull Cooldown<T> testWarmup(@NotNull T key, @NotNull Consumer<Cooldown<T>> onComplete) {
        Location origin = key instanceof Player ? ((Player) key).getLocation() : null;
        Cooldown<T> warmup = this.cooldowns.get(key);

        // Has not warmed up yet, begin warmup
        if (warmup == null || warmup.isActive()) {
            warmup = new Warmup<>(key, this.delayTicks, this.movementThreshold < 0, origin, onComplete);
            this.cooldowns.put(key, warmup);
            this.callbacks.getOnInit().accept(warmup);

            if (key instanceof Player) {
                Messaging.send((Player) key, warmup.messageType(), warmup.message());
            }

            if (warmup.isCancelled()) {
                this.cancel(key);
            }
        }

        return warmup;
    }

    /**
     * Cancels the cooldown associated with the given key.
     *
     * @param key the key corresponding to the cooldown to be canceled
     */
    public void cancel(@NotNull T key) {
        Cooldown<T> cooldown = this.cooldowns.remove(key);
        if (cooldown != null) {
            cooldown.cancel();

            this.callbacks.getOnCancel().accept(cooldown);
        }
    }

    /**
     * Retrieves the cooldown associated with the given key.
     *
     * @param key the key corresponding to the cooldown to be retrieved
     * @return the cooldown associated with the given key, or {@code null} if no cooldown is found
     */
    public @Nullable Cooldown<T> get(@NotNull T key) {
        return this.cooldowns.get(key);
    }

    public static <T> @NotNull Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * @since 0.4.5
     */
    public static final class Builder<T> {

        private int delayTicks;

        private double movementThreshold = 0.1;

        private final CooldownCallbacks<T> callbacks = new CooldownCallbacks<>();

        public @NotNull Builder<T> delay(int ticks) {
            this.delayTicks = ticks;
            return this;
        }

        public @NotNull Builder<T> delay(int time, @NotNull TimeUnit unit) {
            this.delayTicks = Math.toIntExact(unit.toMillis(time) / 50L);
            return this;
        }

        public @NotNull Builder<T> instant() {
            this.delayTicks = -1;
            return this;
        }

        public @NotNull Builder<T> movementThreshold(double threshold) {
            this.movementThreshold = threshold;
            return this;
        }

        public @NotNull Builder<T> allowMovement() {
            this.movementThreshold = -1;
            return this;
        }

        public @NotNull Builder<T> onInit(@NotNull Consumer<Cooldown<T>> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnInit(contextConsumer);
            return this;
        }

        public @NotNull Builder<T> onMove(@NotNull Consumer<Cooldown<T>> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnMove(contextConsumer);
            return this;
        }

        public @NotNull Builder<T> onCountdown(@NotNull Consumer<Cooldown<T>> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnCountdown(contextConsumer);
            return this;
        }

        public @NotNull Builder<T> onComplete(@NotNull Consumer<Cooldown<T>> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnComplete(contextConsumer);
            return this;
        }

        public @NotNull Builder<T> onCancel(@NotNull Consumer<Cooldown<T>> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnCancel(contextConsumer);
            return this;
        }

        public @NotNull CooldownHandler<T> build(@NotNull AlpinePlugin plugin) {
            return new CooldownHandler<>(plugin, this.delayTicks, this.movementThreshold, this.callbacks);
        }
    }
}
