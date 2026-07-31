/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.scheduler.impl;

import co.crystaldev.alpinecore.framework.scheduler.ScheduledTask;
import co.crystaldev.alpinecore.framework.scheduler.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * @since 0.5.0
 */
public final class BukkitTaskScheduler implements TaskScheduler {

    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    // region Sync

    @Override
    public @NotNull ScheduledTask runTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        return wrap(this.scheduler.runTask(plugin, task));
    }

    @Override
    public @NotNull ScheduledTask runTaskLater(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        return wrap(this.scheduler.runTaskLater(plugin, task, delay));
    }

    @Override
    public @NotNull ScheduledTask runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return wrap(this.scheduler.runTaskTimer(plugin, task, delay, period));
    }

    // endregion

    // region Async

    @Override
    public @NotNull ScheduledTask runTaskAsync(@NotNull Plugin plugin, @NotNull Runnable task) {
        return wrap(this.scheduler.runTaskAsynchronously(plugin, task));
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        return wrap(this.scheduler.runTaskLaterAsynchronously(plugin, task, delay));
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return wrap(this.scheduler.runTaskTimerAsynchronously(plugin, task, delay, period));
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        return runTaskLaterAsync(plugin, task, unit.toMillis(delay) / 50L);
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period, @NotNull TimeUnit unit) {
        return runTaskTimerAsync(plugin, task, unit.toMillis(delay) / 50L, unit.toMillis(period) / 50L);
    }

    // endregion

    // region Location

    @Override
    public @NotNull ScheduledTask runTaskAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task) {
        return runTask(plugin, task);
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delay) {
        return runTaskLater(plugin, task, delay);
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delay, long period) {
        return runTaskTimer(plugin, task, delay, period);
    }

    // endregion

    // region Entity

    @Override
    public @NotNull ScheduledTask runTaskForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired) {
        return runTask(plugin, task);
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, long delay) {
        return runTaskLater(plugin, task, delay);
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, long delay, long period) {
        return runTaskTimer(plugin, task, delay, period);
    }

    // endregion

    // region Legacy

    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        return this.scheduler.scheduleSyncDelayedTask(plugin, task);
    }

    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        return this.scheduler.scheduleSyncDelayedTask(plugin, task, delay);
    }

    @Override
    public int scheduleSyncRepeatingTask(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return this.scheduler.scheduleSyncRepeatingTask(plugin, task, delay, period);
    }

    @Override
    public void cancelTask(int taskId) {
        this.scheduler.cancelTask(taskId);
    }

    @Override
    public void cancelTasks(@NotNull Plugin plugin) {
        this.scheduler.cancelTasks(plugin);
    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        return this.scheduler.isCurrentlyRunning(taskId);
    }

    @Override
    public boolean isQueued(int taskId) {
        return this.scheduler.isQueued(taskId);
    }

    // endregion

    private static @NotNull ScheduledTask wrap(@NotNull BukkitTask task) {
        return new BukkitScheduledTask(task);
    }

    private static final class BukkitScheduledTask implements ScheduledTask {
        private final BukkitTask task;
        private volatile boolean cancelled;

        BukkitScheduledTask(@NotNull BukkitTask task) {
            this.task = task;
        }

        @Override
        public @NotNull Plugin plugin() {
            return this.task.getOwner();
        }

        @Override
        public void cancel() {
            this.cancelled = true;
            this.task.cancel();
        }

        @Override
        public boolean isCancelled() {
            return this.cancelled;
        }
    }
}
