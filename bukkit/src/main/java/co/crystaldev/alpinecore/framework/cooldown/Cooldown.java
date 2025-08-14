package co.crystaldev.alpinecore.framework.cooldown;

import co.crystaldev.alpinecore.util.MessageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Represents a cooldown or a warmup for a given entity.
 *
 * @since 0.4.5
 */
@ToString
public class Cooldown<T> {

    private final @NotNull T entity;

    private int remainingTicks;

    private final boolean canMove;

    @Getter(AccessLevel.PACKAGE)
    private final @Nullable Location origin;

    private @NotNull MessageType messageType = MessageType.DISABLED;

    private @Nullable Component message;

    private boolean cancelled;

    Cooldown(@NotNull T entity, int remainingTicks, boolean canMove, @Nullable Location origin) {
        this.entity = entity;
        this.remainingTicks = remainingTicks;
        this.canMove = canMove;
        this.origin = origin;
    }

    /**
     * Retrieves the entity associated with this cooldown.
     *
     * @return The entity.
     */
    public @NotNull T getEntity() {
        return this.entity;
    }

    /**
     * Checks if the cooldown is currently active.
     *
     * @return {@code true} if the cooldown is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return this.remainingTicks > 0;
    }

    /**
     * Checks if the cooldown is in its warmup phase.
     *
     * @return {@code true} if the cooldown is in its warmup phase, {@code false} otherwise.
     */
    public boolean isWarmup() {
        return this instanceof Warmup;
    }

    /**
     * Retrieves the number of remaining ticks for the cooldown.
     *
     * @return the number of remaining ticks
     */
    public int remainingTicks() {
        return this.remainingTicks;
    }

    /**
     * Sets the number of remaining ticks for the cooldown.
     *
     * @param remainingTicks the number of ticks to be set as the remaining for the cooldown
     */
    public void setRemainingTicks(int remainingTicks) {
        this.remainingTicks = remainingTicks;
    }

    /**
     * Calculates the remaining time of the cooldown in the specified time unit.
     *
     * @param unit the time unit in which the remaining time should be converted
     * @return the remaining time in the given time unit.
     */
    public long remainingTime(@NotNull TimeUnit unit) {
        return this.isInstant() ? 0 : unit.convert((this.remainingTicks + 1) * 50L, TimeUnit.MILLISECONDS);
    }

    /**
     * Sets the remaining time for the cooldown.
     *
     * @param time the amount of time to be set as the remaining time for the cooldown
     * @param unit the time unit of the provided time
     */
    public void setRemainingTime(long time, @NotNull TimeUnit unit) {
        this.setRemainingTicks(Math.toIntExact(unit.toMillis(time) / 50L));
    }

    /**
     * Determines whether the cooldown is instant or not.
     *
     * @return {@code true} if the cooldown is instant, {@code false} otherwise
     */
    public boolean isInstant() {
        return this.remainingTicks <= 0;
    }

    /**
     * Sets the cooldown to be instant by setting the number of ticks until teleportation to zero.
     */
    public void setInstant() {
        this.setRemainingTicks(-1);
    }

    /**
     * Determines if the player is allowed to move during cooldown.
     *
     * @return {@code true} if the player can move, {@code false} otherwise
     */
    public boolean canMove() {
        return this.canMove;
    }

    /**
     * Retrieves the type of message associated with the cooldown context.
     *
     * @return the message type associated with the cooldown context.
     */
    public @NotNull MessageType messageType() {
        return this.messageType;
    }

    /**
     * Retrieves the message associated with the cooldown context.
     *
     * @return the message associated with the cooldown context.
     */
    public @Nullable Component message() {
        return this.message;
    }

    /**
     * Sets the type of message associated with the cooldown context.
     *
     * @param messageType the type of message
     */
    public void messageType(@NotNull MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Sets the message associated with the cooldown context.
     *
     * @param message the message to be set
     */
    public void message(@Nullable Component message) {
        if (message != null && this.messageType == MessageType.DISABLED) {
            this.messageType = MessageType.CHAT;
        }
        this.message = message;
    }

    /**
     * Sets the message associated with the cooldown context.
     *
     * @param type    the type of message
     * @param message the message to be set
     */
    public void message(@NotNull MessageType type, @Nullable Component message) {
        this.messageType = type;
        this.message = message;
    }

    /**
     * Clears the message associated with the cooldown context.
     */
    public void clearMessage() {
        this.messageType = MessageType.DISABLED;
        this.message = null;
    }

    /**
     * Determines if the cooldown has been canceled.
     *
     * @return {@code true} if the cooldown is canceled, {@code false} otherwise
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Cancels the cooldown.
     */
    public void cancel() {
        this.cancelled = true;
    }

    int tick() {
        return this.remainingTicks > 0 ? this.remainingTicks-- : 0;
    }

    void complete() {
        // NO OP
    }
}
