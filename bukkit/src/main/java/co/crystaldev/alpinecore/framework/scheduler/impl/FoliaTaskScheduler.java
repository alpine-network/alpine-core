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
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @since 0.5.0
 */
public final class FoliaTaskScheduler implements TaskScheduler {

    // GlobalRegionScheduler
    private final Object globalScheduler;
    private final Method globalRun;
    private final Method globalRunDelayed;
    private final Method globalRunAtFixedRate;
    private final Method globalCancelTasks;

    // AsyncScheduler
    private final Object asyncScheduler;
    private final Method asyncRunNow;
    private final Method asyncRunDelayed;
    private final Method asyncRunAtFixedRate;
    private final Method asyncCancelTasks;

    // RegionScheduler
    private final Object regionScheduler;
    private final Method regionRun;
    private final Method regionRunDelayed;
    private final Method regionRunAtFixedRate;

    // EntityScheduler
    private final Method entityGetScheduler;
    private final Method entitySchedulerRun;
    private final Method entitySchedulerRunDelayed;
    private final Method entitySchedulerRunAtFixedRate;

    // ScheduledTask wrapper helpers
    private final Method scheduledTaskCancel;
    private final Method scheduledTaskGetExecutionState;

    // Legacy compatibility
    private final AtomicInteger nextTaskId = new AtomicInteger(1);
    private final ConcurrentHashMap<Integer, FoliaScheduledTask> taskRegistry = new ConcurrentHashMap<>();

    public FoliaTaskScheduler(@NotNull Server server) {
        try {
            Class<?> serverClass = server.getClass();

            // Acquire scheduler instances
            this.globalScheduler = serverClass.getMethod("getGlobalRegionScheduler").invoke(server);
            this.asyncScheduler = serverClass.getMethod("getAsyncScheduler").invoke(server);
            this.regionScheduler = serverClass.getMethod("getRegionScheduler").invoke(server);

            // Resolve scheduler interface classes (more stable than concrete impl classes)
            Class<?> globalClass = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            Class<?> asyncClass = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            Class<?> regionClass = Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            Class<?> entitySchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            Class<?> scheduledTaskClass = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");

            // GlobalRegionScheduler methods
            this.globalRun = globalClass.getMethod("run", Plugin.class, Consumer.class);
            this.globalRunDelayed = globalClass.getMethod("runDelayed", Plugin.class, Consumer.class, long.class);
            this.globalRunAtFixedRate = globalClass.getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
            this.globalCancelTasks = globalClass.getMethod("cancelTasks", Plugin.class);

            // AsyncScheduler methods
            this.asyncRunNow = asyncClass.getMethod("runNow", Plugin.class, Consumer.class);
            this.asyncRunDelayed = asyncClass.getMethod("runDelayed", Plugin.class, Consumer.class, long.class, TimeUnit.class);
            this.asyncRunAtFixedRate = asyncClass.getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class);
            this.asyncCancelTasks = asyncClass.getMethod("cancelTasks", Plugin.class);

            // RegionScheduler methods
            this.regionRun = regionClass.getMethod("run", Plugin.class, Location.class, Consumer.class);
            this.regionRunDelayed = regionClass.getMethod("runDelayed", Plugin.class, Location.class, Consumer.class, long.class);
            this.regionRunAtFixedRate = regionClass.getMethod("runAtFixedRate", Plugin.class, Location.class, Consumer.class, long.class, long.class);

            // EntityScheduler: getScheduler() is added to Entity by Folia
            this.entityGetScheduler = Entity.class.getMethod("getScheduler");
            this.entitySchedulerRun = entitySchedulerClass.getMethod("run", Plugin.class, Consumer.class, Runnable.class);
            this.entitySchedulerRunDelayed = entitySchedulerClass.getMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class, long.class);
            this.entitySchedulerRunAtFixedRate = entitySchedulerClass.getMethod("runAtFixedRate", Plugin.class, Consumer.class, Runnable.class, long.class, long.class);

            // ScheduledTask methods
            this.scheduledTaskCancel = scheduledTaskClass.getMethod("cancel");
            this.scheduledTaskGetExecutionState = scheduledTaskClass.getMethod("getExecutionState");
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to initialize FoliaTaskScheduler", ex);
        }
    }

    // region Sync

    @Override
    public @NotNull ScheduledTask runTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        return invokeGlobal(this.globalRun, plugin, task);
    }

    @Override
    public @NotNull ScheduledTask runTaskLater(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        return invokeGlobal(this.globalRunDelayed, plugin, task, Math.max(1L, delay));
    }

    @Override
    public @NotNull ScheduledTask runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return invokeGlobal(this.globalRunAtFixedRate, plugin, task, Math.max(1L, delay), period);
    }

    // endregion

    // region Async

    @Override
    public @NotNull ScheduledTask runTaskAsync(@NotNull Plugin plugin, @NotNull Runnable task) {
        return invokeAsync(this.asyncRunNow, plugin, task);
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        return runTaskLaterAsync(plugin, task, delay * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return runTaskTimerAsync(plugin, task, delay * 50L, period * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        return invokeAsyncWithUnit(this.asyncRunDelayed, plugin, task, delay, unit);
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period, @NotNull TimeUnit unit) {
        return invokeAsyncTimerWithUnit(this.asyncRunAtFixedRate, plugin, task, delay, period, unit);
    }

    // endregion

    // region Location

    @Override
    public @NotNull ScheduledTask runTaskAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task) {
        return invokeRegion(this.regionRun, plugin, location, task);
    }

    @Override
    public @NotNull ScheduledTask runTaskLaterAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delay) {
        return invokeRegion(this.regionRunDelayed, plugin, location, task, Math.max(1L, delay));
    }

    @Override
    public @NotNull ScheduledTask runTaskTimerAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delay, long period) {
        return invokeRegion(this.regionRunAtFixedRate, plugin, location, task, Math.max(1L, delay), period);
    }

    // endregion

    // region Entity

    @Override
    public @Nullable ScheduledTask runTaskForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired) {
        return invokeEntity(this.entitySchedulerRun, plugin, entity, task, retired);
    }

    @Override
    public @Nullable ScheduledTask runTaskLaterForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, long delay) {
        return invokeEntity(this.entitySchedulerRunDelayed, plugin, entity, task, retired, Math.max(1L, delay));
    }

    @Override
    public @Nullable ScheduledTask runTaskTimerForEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, long delay, long period) {
        return invokeEntity(this.entitySchedulerRunAtFixedRate, plugin, entity, task, retired, Math.max(1L, delay), period);
    }

    // endregion

    // region Legacy

    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        return scheduleSyncDelayedTask(plugin, task, 1L);
    }

    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable task, long delay) {
        int id = nextTaskId.getAndIncrement();
        try {
            // Wrap task so we can complete registration before it runs
            Runnable[] wrapper = new Runnable[1];
            wrapper[0] = () -> {
                try {
                    task.run();
                } finally {
                    taskRegistry.remove(id);
                }
            };
            Consumer<?> consumer = t -> wrapper[0].run();
            Object nativeTask = this.globalRunDelayed.invoke(this.globalScheduler, plugin, consumer, Math.max(1L, delay));
            if (nativeTask != null) {
                this.taskRegistry.put(id, new FoliaScheduledTask(nativeTask));
                return id;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to schedule delayed task", ex);
        }
        return -1;
    }

    @Override
    public int scheduleSyncRepeatingTask(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        int id = nextTaskId.getAndIncrement();
        try {
            Consumer<?> consumer = t -> task.run();
            Object nativeTask = this.globalRunAtFixedRate.invoke(this.globalScheduler, plugin, consumer, Math.max(1L, delay), period);
            if (nativeTask != null) {
                this.taskRegistry.put(id, new FoliaScheduledTask(nativeTask));
                return id;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to schedule repeating task", ex);
        }
        return -1;
    }

    @Override
    public void cancelTask(int taskId) {
        FoliaScheduledTask task = this.taskRegistry.remove(taskId);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void cancelTasks(@NotNull Plugin plugin) {
        // Cancel registry tasks for this plugin (best-effort; individual tasks don't track owner)
        this.taskRegistry.values().forEach(ScheduledTask::cancel);
        this.taskRegistry.clear();

        // Also cancel via native Folia schedulers
        try {
            this.globalCancelTasks.invoke(this.globalScheduler, plugin);
            this.asyncCancelTasks.invoke(this.asyncScheduler, plugin);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to cancel tasks", ex);
        }
    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        FoliaScheduledTask task = this.taskRegistry.get(taskId);
        if (task == null) return false;
        return "RUNNING".equals(task.getExecutionStateName());
    }

    @Override
    public boolean isQueued(int taskId) {
        FoliaScheduledTask task = this.taskRegistry.get(taskId);
        if (task == null) return false;
        return "IDLE".equals(task.getExecutionStateName());
    }

    // endregion

    // region Helpers

    private @NotNull FoliaScheduledTask invokeGlobal(@NotNull Method method, @NotNull Plugin plugin, @NotNull Runnable task, Object... extra) {
        try {
            Consumer<?> consumer = t -> task.run();
            Object[] args = buildArgs(plugin, consumer, extra);
            Object result = method.invoke(this.globalScheduler, args);
            return new FoliaScheduledTask(result);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to invoke GlobalRegionScheduler." + method.getName(), ex);
        }
    }

    private @NotNull FoliaScheduledTask invokeAsync(@NotNull Method method, @NotNull Plugin plugin, @NotNull Runnable task) {
        try {
            Consumer<?> consumer = t -> task.run();
            Object result = method.invoke(this.asyncScheduler, plugin, consumer);
            return new FoliaScheduledTask(result);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to invoke AsyncScheduler." + method.getName(), ex);
        }
    }

    private @NotNull FoliaScheduledTask invokeAsyncWithUnit(@NotNull Method method, @NotNull Plugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        try {
            Consumer<?> consumer = t -> task.run();
            Object result = method.invoke(this.asyncScheduler, plugin, consumer, delay, unit);
            return new FoliaScheduledTask(result);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to invoke AsyncScheduler." + method.getName(), ex);
        }
    }

    private @NotNull FoliaScheduledTask invokeAsyncTimerWithUnit(@NotNull Method method, @NotNull Plugin plugin, @NotNull Runnable task, long delay, long period, @NotNull TimeUnit unit) {
        try {
            Consumer<?> consumer = t -> task.run();
            Object result = method.invoke(this.asyncScheduler, plugin, consumer, delay, period, unit);
            return new FoliaScheduledTask(result);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to invoke AsyncScheduler." + method.getName(), ex);
        }
    }

    private @NotNull FoliaScheduledTask invokeRegion(@NotNull Method method, @NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, Object... extra) {
        try {
            Consumer<?> consumer = t -> task.run();
            Object[] args = buildArgs(plugin, location, consumer, extra);
            Object result = method.invoke(this.regionScheduler, args);
            return new FoliaScheduledTask(result);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to invoke RegionScheduler." + method.getName(), ex);
        }
    }

    private @Nullable FoliaScheduledTask invokeEntity(@NotNull Method method, @NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired, Object... extra) {
        try {
            Object entityScheduler = this.entityGetScheduler.invoke(entity);
            Consumer<?> consumer = t -> task.run();
            Object[] args = buildArgs(plugin, consumer, retired, extra);
            Object result = method.invoke(entityScheduler, args);
            return result != null ? new FoliaScheduledTask(result) : null;
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to invoke EntityScheduler." + method.getName(), ex);
        }
    }

    private static Object[] buildArgs(Object... parts) {
        int count = 0;
        for (Object p : parts) {
            if (p instanceof Object[]) {
                count += ((Object[]) p).length;
            }
            else {
                count++;
            }
        }
        Object[] result = new Object[count];
        int i = 0;
        for (Object p : parts) {
            if (p instanceof Object[]) {
                for (Object item : (Object[]) p) {
                    result[i++] = item;
                }
            }
            else {
                result[i++] = p;
            }
        }
        return result;
    }

    // endregion

    private final class FoliaScheduledTask implements ScheduledTask {
        private final Object nativeTask;

        FoliaScheduledTask(@NotNull Object nativeTask) {
            this.nativeTask = nativeTask;
        }

        @Override
        public void cancel() {
            try {
                scheduledTaskCancel.invoke(this.nativeTask);
            }
            catch (Exception ex) {
                throw new RuntimeException("Failed to cancel Folia task", ex);
            }
        }

        @Override
        public boolean isCancelled() {
            String state = getExecutionStateName();
            return "CANCELLED".equals(state) || "CANCELLED_RUNNING".equals(state);
        }

        String getExecutionStateName() {
            try {
                Object state = scheduledTaskGetExecutionState.invoke(this.nativeTask);
                return state != null ? state.toString() : "FINISHED";
            }
            catch (Exception ex) {
                return "FINISHED";
            }
        }
    }
}
