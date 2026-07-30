/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Server-wide scheduler abstraction that works on both standard Bukkit/Paper
 * and Folia's region-threaded model.
 *
 * @see co.crystaldev.alpinecore.AlpinePlugin#scheduler()
 * @since 0.5.0
 */
public interface TaskScheduler {

    // region Sync

    /**
     * Schedules a task to run on the next server tick.
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTask(@NotNull Plugin plugin, @NotNull Runnable task);

    /**
     * Schedules a task to run after a delay.
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @param delay  delay in ticks before execution
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskLater(@NotNull Plugin plugin, @NotNull Runnable task, long delay);

    /**
     * Schedules a task to run repeatedly at a fixed rate.
     *
     * @param plugin  the owning plugin
     * @param task    the task to run
     * @param delay   initial delay in ticks
     * @param period  period in ticks between executions
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period);

    // endregion

    // region Async

    /**
     * Schedules a task to run asynchronously on the next tick.
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskAsync(@NotNull Plugin plugin, @NotNull Runnable task);

    /**
     * Schedules a task to run asynchronously after a delay.
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @param delay  delay in ticks before execution
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay);

    /**
     * Schedules an async task to run repeatedly at a fixed rate.
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @param delay  initial delay in ticks
     * @param period period in ticks between executions
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskTimerAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period);

    /**
     * Schedules a task to run asynchronously after a delay specified with a {@link TimeUnit}.
     * <p>
     * On Folia, this maps directly to the {@code AsyncScheduler}. On Bukkit,
     * the delay is converted to ticks (1 tick = 50 ms).
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @param delay  delay before execution
     * @param unit   time unit for the delay
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit unit);

    /**
     * Schedules an async task to run repeatedly at a fixed rate, specified with a {@link TimeUnit}.
     * <p>
     * On Folia, this maps directly to the {@code AsyncScheduler}. On Bukkit,
     * values are converted to ticks (1 tick = 50 ms).
     *
     * @param plugin  the owning plugin
     * @param task    the task to run
     * @param delay   initial delay before first execution
     * @param period  period between executions
     * @param unit    time unit for delay and period
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskTimerAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period, @NotNull TimeUnit unit);

    // endregion

    // region Location

    /**
     * Schedules a task to run on the tick thread owning the given location.
     * <p>
     * On Folia, this uses the {@code RegionScheduler}. On Bukkit, this is
     * equivalent to {@link #runTask(Plugin, Runnable)}.
     *
     * @param plugin   the owning plugin
     * @param location the location whose region owns the task
     * @param task     the task to run
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task);

    /**
     * Schedules a task to run after a delay on the tick thread owning the given location.
     *
     * @param plugin   the owning plugin
     * @param location the location whose region owns the task
     * @param task     the task to run
     * @param delay    delay in ticks before execution
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskLaterAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delay);

    /**
     * Schedules a task to run repeatedly on the tick thread owning the given location.
     *
     * @param plugin   the owning plugin
     * @param location the location whose region owns the task
     * @param task     the task to run
     * @param delay    initial delay in ticks
     * @param period   period in ticks between executions
     * @return the scheduled task
     */
    @NotNull ScheduledTask runTaskTimerAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delay, long period);

    // endregion

    // region Entity

    /**
     * Schedules a task to run on the tick thread that owns the given entity.
     * <p>
     * On Folia, this uses the {@code EntityScheduler}. On Bukkit, this is
     * equivalent to {@link #runTask(Plugin, Runnable)}.
     *
     * @param plugin  the owning plugin
     * @param entity  the entity whose thread owns the task
     * @param task    the task to run
     * @param retired callback invoked if the entity is removed before the task runs;
     *                may be {@code null}. Only used on Folia.
     * @return the scheduled task, or {@code null} if the entity is already removed (Folia only)
     */
    @Nullable ScheduledTask runTaskForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired);

    /**
     * Schedules a task to run after a delay on the tick thread that owns the given entity.
     *
     * @param plugin  the owning plugin
     * @param entity  the entity whose thread owns the task
     * @param task    the task to run
     * @param retired callback invoked if the entity is removed before the task runs;
     *                may be {@code null}. Only used on Folia.
     * @param delay   delay in ticks before execution
     * @return the scheduled task, or {@code null} if the entity is already removed (Folia only)
     */
    @Nullable ScheduledTask runTaskLaterForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, long delay);

    /**
     * Schedules a task to run repeatedly on the tick thread that owns the given entity.
     *
     * @param plugin  the owning plugin
     * @param entity  the entity whose thread owns the task
     * @param task    the task to run
     * @param retired callback invoked if the entity is removed before the task runs;
     *                may be {@code null}. Only used on Folia.
     * @param delay   initial delay in ticks
     * @param period  period in ticks between executions
     * @return the scheduled task, or {@code null} if the entity is already removed (Folia only)
     */
    @Nullable ScheduledTask runTaskTimerForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, long delay, long period);

    // endregion

    // region Legacy

    /**
     * Schedules a sync task to run on the next tick, returning a numeric task ID.
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @return the numeric task ID, or {@code -1} on failure
     */
    int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable task);

    /**
     * Schedules a sync task to run after a delay, returning a numeric task ID.
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @param delay  delay in ticks before execution
     * @return the numeric task ID, or {@code -1} on failure
     */
    int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable task, long delay);

    /**
     * Schedules a sync task to run repeatedly, returning a numeric task ID.
     *
     * @param plugin the owning plugin
     * @param task   the task to run
     * @param delay  initial delay in ticks
     * @param period period in ticks between executions
     * @return the numeric task ID, or {@code -1} on failure
     */
    int scheduleSyncRepeatingTask(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period);

    /**
     * Cancels the task with the given numeric ID.
     *
     * @param taskId the task ID to cancel
     */
    void cancelTask(int taskId);

    /**
     * Cancels all tasks owned by the given plugin.
     *
     * @param plugin the owning plugin
     */
    void cancelTasks(@NotNull Plugin plugin);

    /**
     * Returns whether the task with the given ID is currently executing.
     *
     * @param taskId the task ID to check
     * @return {@code true} if currently running
     */
    boolean isCurrentlyRunning(int taskId);

    /**
     * Returns whether the task with the given ID is queued to run.
     *
     * @param taskId the task ID to check
     * @return {@code true} if queued
     */
    boolean isQueued(int taskId);

    // endregion
}
