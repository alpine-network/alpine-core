/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.scheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Emulates Bukkit's integer task IDs on top of a scheduler that hands out object handles.
 *
 * @since 0.5.0
 */
public final class LegacyTaskIds {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final Map<Integer, Handle> registry = new ConcurrentHashMap<>();

    /**
     * Allocates an ID, schedules the task and registers the resulting handle.
     * <p>
     * The registry entry is published <em>before</em> the task is handed to the scheduler. On a
     * region-threaded server a task submitted for the next tick can begin, and callers can observe
     * it, before the scheduling call has even returned; registering afterwards leaves a window in
     * which {@code isQueued} and {@code cancelTask} report the task as unknown.
     *
     * @param plugin    the owning plugin
     * @param task      the work to run
     * @param oneShot   whether the task deregisters itself once it has run
     * @param scheduler schedules the (wrapped) runnable and returns its handle
     * @return the task ID, or {@code -1} if the scheduler declined to schedule the task
     */
    public int schedule(@NotNull Plugin plugin, @NotNull Runnable task, boolean oneShot,
                        @NotNull Function<Runnable, ScheduledTask> scheduler) {
        int id = this.nextId.getAndIncrement();
        Handle handle = new Handle(plugin);
        this.registry.put(id, handle);

        Runnable wrapped = oneShot ? () -> {
            try {
                task.run();
            }
            finally {
                this.registry.remove(id, handle);
            }
        } : task;

        ScheduledTask delegate;
        try {
            delegate = scheduler.apply(wrapped);
        }
        catch (RuntimeException | Error ex) {
            this.registry.remove(id, handle);
            throw ex;
        }

        if (delegate == null) {
            this.registry.remove(id, handle);
            return -1;
        }

        // If cancel() arrived while the scheduler was running, bind() cancels the delegate for us.
        handle.bind(delegate);
        if (handle.isCancelled()) {
            this.registry.remove(id, handle);
        }
        return id;
    }

    /**
     * Cancels the task with the given ID, if it is still registered.
     *
     * @param id the task ID
     */
    public void cancel(int id) {
        Handle handle = this.registry.remove(id);
        if (handle != null) {
            handle.cancel();
        }
    }

    /**
     * Cancels every registered task owned by the given plugin.
     * <p>
     * Note that this only reaches tasks that went through {@link #schedule}. Folia exposes no bulk
     * cancel for its region and entity schedulers, so tasks from {@code runTaskAtLocation} and
     * {@code runTaskForEntity} are not covered - the same limitation Bukkit's own
     * {@code cancelTasks} has always had here.
     *
     * @param plugin the owning plugin
     */
    public void cancelAll(@NotNull Plugin plugin) {
        Iterator<Map.Entry<Integer, Handle>> iterator = this.registry.entrySet().iterator();
        while (iterator.hasNext()) {
            Handle handle = iterator.next().getValue();
            if (plugin.equals(handle.plugin())) {
                handle.cancel();
                iterator.remove();
            }
        }
    }

    /**
     * @param id the task ID
     * @return the registered task, or {@code null} if the ID is unknown
     */
    public @Nullable ScheduledTask get(int id) {
        return this.registry.get(id);
    }

    /**
     * @param id the task ID
     * @return whether the task is currently executing
     */
    public boolean isCurrentlyRunning(int id) {
        Handle handle = this.registry.get(id);
        return handle != null && handle.getState() == ScheduledTask.State.RUNNING;
    }

    /**
     * @param id the task ID
     * @return whether the task is scheduled but not yet executing
     */
    public boolean isQueued(int id) {
        Handle handle = this.registry.get(id);
        return handle != null && handle.getState() == ScheduledTask.State.IDLE;
    }

    /**
     * A task ID's registry entry.
     * <p>
     * Exists from the moment the ID is allocated, so it can absorb a {@code cancel()} that races
     * ahead of the underlying task being created.
     */
    private static final class Handle implements ScheduledTask {

        private final Plugin plugin;

        private final Object lock = new Object();

        private ScheduledTask delegate;

        private boolean cancelled;

        Handle(@NotNull Plugin plugin) {
            this.plugin = plugin;
        }

        void bind(@NotNull ScheduledTask delegate) {
            boolean cancelEagerly;
            synchronized (this.lock) {
                this.delegate = delegate;
                cancelEagerly = this.cancelled;
            }

            if (cancelEagerly) {
                delegate.cancel();
            }
        }

        @Override
        public @NotNull Plugin plugin() {
            return this.plugin;
        }

        @Override
        public void cancel() {
            ScheduledTask delegate;
            synchronized (this.lock) {
                this.cancelled = true;
                delegate = this.delegate;
            }

            if (delegate != null) {
                delegate.cancel();
            }
        }

        @Override
        public boolean isCancelled() {
            ScheduledTask delegate;
            synchronized (this.lock) {
                if (this.cancelled) {
                    return true;
                }
                delegate = this.delegate;
            }
            return delegate != null && delegate.isCancelled();
        }

        @Override
        public @NotNull State getState() {
            ScheduledTask delegate;
            synchronized (this.lock) {
                if (this.cancelled) {
                    return State.CANCELLED;
                }
                delegate = this.delegate;
            }
            // Not yet bound: allocated and scheduled, but no handle to interrogate.
            return delegate == null ? State.IDLE : delegate.getState();
        }
    }
}
