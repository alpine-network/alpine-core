/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
@ToString
public final class TeleportContext {

    private final @NotNull Player player;

    private @NotNull Location destination;

    private int ticksUntilTeleport;

    private final boolean canMove;

    private @NotNull MessageType messageType = MessageType.DISABLED;

    private @Nullable Component message;

    private boolean cancelled;

    TeleportContext(@NotNull Player player, @NotNull Location destination, int ticksUntilTeleport, boolean canMove) {
        this.player = player;
        this.destination = destination;
        this.ticksUntilTeleport = ticksUntilTeleport;
        this.canMove = canMove;
    }

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
     * Sets the number of ticks remaining until the teleportation occurs.
     *
     * @param ticksUntilTeleport the number of ticks until the teleportation takes place
     */
    public void setTicksUntilTeleport(int ticksUntilTeleport) {
        this.ticksUntilTeleport = ticksUntilTeleport;
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
     * Sets the time remaining until the teleportation occurs.
     *
     * @param time the time duration
     * @param unit the time unit of the duration
     */
    public void setTimeUntilTeleport(long time, @NotNull TimeUnit unit) {
        this.setTicksUntilTeleport(Math.toIntExact(unit.toMillis(time) / 50L));
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
     * Sets the teleportation to be instant by setting the number of ticks until teleportation to zero.
     * After calling this method, the teleportation will occur immediately.
     */
    public void setInstant() {
        this.ticksUntilTeleport = -1;
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
