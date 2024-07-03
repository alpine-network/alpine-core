package co.crystaldev.alpinecore.framework.teleport;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The TeleportTask class represents a teleport task for a player.
 * It allows for delayed teleportation with customizable callbacks and movement restrictions.
 * </p>
 * Example usage:
 * <pre>{@code
 * TeleportTask task = TeleportTask.builder(player, destination)
 *         .delay(5, TimeUnit.SECONDS)
 *         .resetPitchAndYaw()
 *         .onTeleport(ctx -> {
 *              ctx.message(Component.text("Teleporting!"));
 *         });
 * alpinePlugin.getTeleportManager().initiateTeleport(task);
 * }</pre>
 *
 * @since 0.4.0
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Getter(AccessLevel.PACKAGE)
public final class TeleportTask {

    private final Player player;

    private final Location origin, destination;

    private final TeleportCallbacks callbacks;

    private final int delay;

    private int ticksToTeleport;

    private double movementThreshold;

    /**
     * Retrieves the time remaining until teleportation in the specified time unit.
     *
     * @param unit the time unit to convert the result to
     * @return the time remaining until teleportation in the specified time unit
     */
    public long getTimeUntilTeleport(@NotNull TimeUnit unit) {
        return unit.convert(this.delay, TimeUnit.MILLISECONDS);
    }

    int tick() {
        return this.ticksToTeleport--;
    }

    boolean canMove() {
        return this.movementThreshold < 0;
    }

    @NotNull TeleportContext createContext(boolean instant) {
        if (instant) {
            return new TeleportContext(this.player, this.destination, -1, this.canMove());
        }
        else {
            return new TeleportContext(this.player, this.destination, this.ticksToTeleport, this.canMove());
        }
    }

    public static @NotNull Builder builder(@NotNull Player player, @NotNull Location destination) {
        return new Builder(player, destination);
    }

    /**
     * @since 0.4.0
     */
    @RequiredArgsConstructor
    public static final class Builder {

        private final Player player;

        private final Location destination;

        private final TeleportCallbacks callbacks = new TeleportCallbacks();

        private int delayTicks, delayMs;

        private double movementThreshold = 0.1;

        public @NotNull Builder delay(int ticks) {
            this.delayTicks = ticks;
            this.delayMs = ticks * 50;
            return this;
        }

        public @NotNull Builder delay(int time, @NotNull TimeUnit unit) {
            this.delayMs = (int) unit.toMillis(time);
            this.delayTicks = this.delayMs / 50;
            return this;
        }

        public @NotNull Builder instant() {
            this.delayTicks = -1;
            return this;
        }

        public @NotNull Builder movementThreshold(double threshold) {
            this.movementThreshold = threshold;
            return this;
        }

        public @NotNull Builder allowMovement() {
            this.movementThreshold = -1;
            return this;
        }

        public @NotNull Builder resetPitchAndYaw() {
            this.destination.setPitch(0.0F);
            this.destination.setYaw(0.0F);
            return this;
        }

        public @NotNull Builder onInit(@NotNull Consumer<TeleportContext> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnInit(contextConsumer);
            return this;
        }

        public @NotNull Builder onMove(@NotNull Consumer<TeleportContext> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnMove(contextConsumer);
            return this;
        }

        public @NotNull Builder onCountdown(@NotNull Consumer<TeleportContext> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnCountdown(contextConsumer);
            return this;
        }

        public @NotNull Builder onTeleport(@NotNull Consumer<TeleportContext> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnTeleport(contextConsumer);
            return this;
        }

        public @NotNull Builder onCancel(@NotNull Consumer<TeleportContext> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnCancel(contextConsumer);
            return this;
        }

        public @NotNull TeleportTask build() {
            return new TeleportTask(this.player, this.player.getLocation(), this.destination, this.callbacks, this.delayTicks, this.delayMs, this.movementThreshold);
        }
    }
}
