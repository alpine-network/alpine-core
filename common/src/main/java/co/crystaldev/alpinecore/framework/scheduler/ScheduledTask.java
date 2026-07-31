/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.scheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a task scheduled to run on the server.
 *
 * @since 0.5.0
 */
public interface ScheduledTask {

    /**
     * Retrieves the owning plugin associated with this task.
     *
     * @return the plugin that scheduled this task
     */
    @NotNull Plugin plugin();

    /**
     * Cancels this task. Has no effect if the task is already canceled or finished.
     */
    void cancel();

    /**
     * Returns whether this task has been canceled.
     *
     * @return {@code true} if canceled
     */
    boolean isCancelled();

    /**
     * Returns the current lifecycle state of this task.
     *
     * @return the state, never {@code null}
     */
    default @NotNull State getState() {
        return this.isCancelled() ? State.CANCELLED : State.IDLE;
    }

    /**
     * The lifecycle of a scheduled task.
     * <p>
     * Mirrors Folia's {@code ScheduledTask.ExecutionState} one-for-one, so mapping between them is
     * a {@code valueOf} rather than a string comparison - a rename upstream then surfaces as a
     * missing constant at compile time on the Paper distribution instead of silently reporting
     * every task as finished.
     *
     * @since 0.5.0
     */
    enum State {

        /** Scheduled, but not currently executing. */
        IDLE,

        /** Currently executing. */
        RUNNING,

        /** Executed to completion and will not run again. */
        FINISHED,

        /** Cancelled, and not executing. */
        CANCELLED,

        /** Cancelled while executing; the current run will complete but no further runs occur. */
        CANCELLED_RUNNING;

        /**
         * @return whether this state means the task was cancelled
         */
        public boolean isCancelled() {
            return this == CANCELLED || this == CANCELLED_RUNNING;
        }

        /**
         * Resolves a state by name, tolerating an unrecognized one.
         *
         * @param name the state name, possibly {@code null}
         * @return the matching state, or {@link #FINISHED} if there is none
         */
        public static @NotNull State byName(@Nullable String name) {
            if (name != null) {
                for (State state : State.values()) {
                    if (state.name().equals(name)) {
                        return state;
                    }
                }
            }
            return FINISHED;
        }
    }
}
