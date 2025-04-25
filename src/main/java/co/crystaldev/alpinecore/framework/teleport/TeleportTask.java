package co.crystaldev.alpinecore.framework.teleport;

import co.crystaldev.alpinecore.AlpinePlugin;
import lombok.*;
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

    private int ticksToTeleport;

    private double movementThreshold;

    int tick() {
        return this.ticksToTeleport <= 0 ? this.ticksToTeleport : this.ticksToTeleport--;
    }

    boolean canMove() {
        return this.ticksToTeleport <= 0 || this.movementThreshold < 0;
    }

    void apply(@NotNull TeleportContext context) {
        this.ticksToTeleport = context.ticksUntilTeleport();
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

        private int delayTicks;

        private double movementThreshold = 0.1;

        public @NotNull Builder delay(int ticks) {
            this.delayTicks = ticks;
            return this;
        }

        public @NotNull Builder delay(int time, @NotNull TimeUnit unit) {
            this.delayTicks = Math.toIntExact(unit.toMillis(time) / 50L);
            return this;
        }

        public @NotNull Builder instant() {
            this.delayTicks = -1;
            return this;
        }

        public @NotNull Builder instant(boolean instant) {
            return instant ? this.instant() : this;
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

        public @NotNull Builder onApply(@NotNull Consumer<TeleportContext> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnApply(contextConsumer);
            return this;
        }

        public @NotNull Builder onDamage(@NotNull Consumer<TeleportContext> contextConsumer) {
            Validate.notNull(contextConsumer, "contextConsumer cannot be null");
            this.callbacks.setOnDamage(contextConsumer);
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
            return new TeleportTask(this.player, this.player.getLocation(), this.destination, this.callbacks, this.delayTicks, this.movementThreshold);
        }

        public @NotNull TeleportTask initiate(@NotNull AlpinePlugin plugin) {
            TeleportTask task = this.build();
            plugin.getTeleportManager().initiateTeleport(task);
            return task;
        }
    }
}
