package co.crystaldev.alpinecore.framework.teleport;

import co.crystaldev.alpinecore.util.MessageType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Represents a context containing information about a teleportation process.
 *
 * @since 0.4.0
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE) @ToString
public final class TeleportContext {

    private final @NotNull Player player;

    private @NotNull Location destination;

    private final int ticksUntilTeleport;

    private final boolean canMove;

    private @NotNull MessageType messageType = MessageType.DISABLED;

    private @Nullable Component message;

    private boolean cancelled;

    /**
     * Retrieves the player associated with the teleportation.
     *
     * @return the player associated with the teleportation
     */
    public @NotNull Player player() {
        return this.player;
    }

    /**
     * Retrieves the destination location associated with the teleportation.
     *
     * @return the destination location
     */
    public @NotNull Location destination() {
        return this.destination;
    }

    /**
     * Sets the destination location for the teleportation process.
     *
     * @param destination the new destination location to be set
     */
    public void destination(@NotNull Location destination) {
        this.destination = destination;
    }

    /**
     * Retrieves the number of ticks remaining until the teleportation occurs.
     *
     * @return the number of ticks until teleportation
     */
    public int ticksUntilTeleport() {
        return this.ticksUntilTeleport;
    }

    /**
     * Calculates the time remaining until teleportation.
     *
     * @param unit the time unit to convert the result to
     * @return the time remaining until teleportation in the specified time unit
     */
    public long timeUntilTeleport(@NotNull TimeUnit unit) {
        return this.isInstant() ? 0 : unit.convert((this.ticksUntilTeleport + 1) * 50L, TimeUnit.MILLISECONDS);
    }

    /**
     * Determines whether the teleportation is instant or not.
     *
     * @return {@code true} if the teleportation is instant, {@code false} otherwise
     */
    public boolean isInstant() {
        return this.ticksUntilTeleport <= 0;
    }

    /**
     * Retrieves whether the player can move during the teleportation process.
     *
     * @return {@code true} if the player can move, {@code false} otherwise
     */
    public boolean canMove() {
        return this.canMove;
    }

    /**
     * Retrieves the message type associated with the teleportation.
     *
     * @return the message type
     */
    public @NotNull MessageType messageType() {
        return this.messageType;
    }

    /**
     * Retrieves the message associated with the teleportation.
     *
     * @return the message associated with the teleportation
     */
    public @Nullable Component message() {
        return this.message;
    }

    /**
     * Sets the message type associated with the teleportation.
     *
     * @param messageType the message type to be set
     */
    public void messageType(@NotNull MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Sets the message associated with the teleportation.
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
     * Sets the message type and content associated with the teleportation.
     *
     * @param type    the message type to be set
     * @param message the message to be set
     */
    public void message(@NotNull MessageType type, @Nullable Component message) {
        this.messageType = type;
        this.message = message;
    }

    /**
     * Retrieves the cancellation status of the teleportation process.
     *
     * @return {@code true} if the teleportation process is canceled, {@code false} otherwise
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Cancels the ongoing teleportation process.
     * After calling this method, the teleportation will be marked as canceled.
     */
    public void cancelTeleport() {
        this.cancelled = true;
    }
}
