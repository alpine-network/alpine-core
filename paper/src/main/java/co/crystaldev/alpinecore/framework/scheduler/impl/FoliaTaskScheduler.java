/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.scheduler.impl;

import co.crystaldev.alpinecore.framework.scheduler.LegacyTaskIds;
import co.crystaldev.alpinecore.framework.scheduler.ScheduledTask;
import co.crystaldev.alpinecore.framework.scheduler.TaskScheduler;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @since 0.5.0
 */
public final class FoliaTaskScheduler implements TaskScheduler {

    private final GlobalRegionScheduler globalScheduler;

    private final AsyncScheduler asyncScheduler;

    private final RegionScheduler regionScheduler;

    private final LegacyTaskIds legacyTasks = new LegacyTaskIds();

    public FoliaTaskScheduler(@NotNull Server server) {
        this.globalScheduler = server.getGlobalRegionScheduler();
        this.asyncScheduler = server.getAsyncScheduler();
        this.regionScheduler = server.getRegionScheduler();
    }

    // region Sync

    @Override
    public @NotNull ScheduledTask runTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        return wrap(plugin, this.globalScheduler.run(plugin, consumer(task)));
    }

    @Override
    public @NotNull ScheduledTask runTaskLater(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        return wrap(plugin, this.globalScheduler.runDelayed(plugin, consumer(task), Math.max(1L, delay)));
    }

    @Override
    public @NotNull ScheduledTask runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return wrap(plugin, this.globalScheduler.runAtFixedRate(plugin, consumer(task), Math.max(1L, delay), period));
    }

    // endregion

    // region Async

    @Override
    public @NotNull ScheduledTask runTaskAsync(@NotNull Plugin plugin, @NotNull Runnable task) {
        return wrap(plugin, this.asyncScheduler.runNow(plugin, consumer(task)));
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        return this.runTaskLaterAsync(plugin, task, delay * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return this.runTaskTimerAsync(plugin, task, delay * 50L, period * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        return wrap(plugin, this.asyncScheduler.runDelayed(plugin, consumer(task), Math.max(1L, delay), unit));
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period, @NotNull TimeUnit unit) {
        return wrap(plugin, this.asyncScheduler.runAtFixedRate(plugin, consumer(task),
                Math.max(1L, delay), Math.max(1L, period), unit));
    }

    // endregion

    // region Location

    @Override
    public @NotNull ScheduledTask runTaskAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task) {
        return wrap(plugin, this.regionScheduler.run(plugin, location, consumer(task)));
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delay) {
        return wrap(plugin, this.regionScheduler.runDelayed(plugin, location, consumer(task), Math.max(1L, delay)));
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delay, long period) {
        return wrap(plugin, this.regionScheduler.runAtFixedRate(plugin, location, consumer(task), Math.max(1L, delay), period));
    }

    // endregion

    // region Entity

    @Override
    public @Nullable ScheduledTask runTaskForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired) {
        EntityScheduler scheduler = entity.getScheduler();
        return wrapNullable(plugin, scheduler.run(plugin, consumer(task), retired));
    }

    @Override
    public @Nullable ScheduledTask runTaskLaterForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, long delay) {
        EntityScheduler scheduler = entity.getScheduler();
        return wrapNullable(plugin, scheduler.runDelayed(plugin, consumer(task), retired, Math.max(1L, delay)));
    }

    @Override
    public @Nullable ScheduledTask runTaskTimerForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, long delay, long period) {
        EntityScheduler scheduler = entity.getScheduler();
        return wrapNullable(plugin, scheduler.runAtFixedRate(plugin, consumer(task), retired, Math.max(1L, delay), period));
    }

    // endregion

    // region Legacy

    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        return this.scheduleSyncDelayedTask(plugin, task, 1L);
    }

    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        return this.legacyTasks.schedule(plugin, task, true, wrapped ->
                wrap(plugin, this.globalScheduler.runDelayed(plugin, consumer(wrapped), Math.max(1L, delay))));
    }

    @Override
    public int scheduleSyncRepeatingTask(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return this.legacyTasks.schedule(plugin, task, false, wrapped ->
                wrap(plugin, this.globalScheduler.runAtFixedRate(plugin, consumer(wrapped), Math.max(1L, delay), period)));
    }

    @Override
    public void cancelTask(int taskId) {
        this.legacyTasks.cancel(taskId);
    }

    @Override
    public void cancelTasks(@NotNull Plugin plugin) {
        this.legacyTasks.cancelAll(plugin);

        // Folia offers no bulk cancel for the region or entity schedulers, so tasks scheduled via
        // runTaskAtLocation/runTaskForEntity are not reached here.
        this.globalScheduler.cancelTasks(plugin);
        this.asyncScheduler.cancelTasks(plugin);
    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        return this.legacyTasks.isCurrentlyRunning(taskId);
    }

    @Override
    public boolean isQueued(int taskId) {
        return this.legacyTasks.isQueued(taskId);
    }

    // endregion

    private static @NotNull Consumer<io.papermc.paper.threadedregions.scheduler.ScheduledTask> consumer(@NotNull Runnable task) {
        return ignored -> task.run();
    }

    private static @NotNull ScheduledTask wrap(
            @NotNull Plugin plugin,
            @NotNull io.papermc.paper.threadedregions.scheduler.ScheduledTask task
    ) {
        return new FoliaScheduledTask(plugin, task);
    }

    private static @Nullable ScheduledTask wrapNullable(
            @NotNull Plugin plugin,
            @Nullable io.papermc.paper.threadedregions.scheduler.ScheduledTask task
    ) {
        return task == null ? null : new FoliaScheduledTask(plugin, task);
    }

    private record FoliaScheduledTask(
            Plugin plugin,
            io.papermc.paper.threadedregions.scheduler.ScheduledTask nativeTask
    ) implements ScheduledTask {

            private FoliaScheduledTask(
                    @NotNull Plugin plugin,
                    @NotNull io.papermc.paper.threadedregions.scheduler.ScheduledTask nativeTask
            ) {
                this.plugin = plugin;
                this.nativeTask = nativeTask;
            }

            @Override
            public void cancel() {
                this.nativeTask.cancel();
            }

            @Override
            public boolean isCancelled() {
                return this.getState().isCancelled();
            }

            @Override
            public @NotNull State getState() {
                return switch (this.nativeTask.getExecutionState()) {
                    case IDLE -> State.IDLE;
                    case RUNNING -> State.RUNNING;
                    case CANCELLED -> State.CANCELLED;
                    case CANCELLED_RUNNING -> State.CANCELLED_RUNNING;
                    default -> State.FINISHED;
                };
            }
        }
}
