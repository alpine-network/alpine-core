/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.scheduler;

/**
 * Represents a task scheduled to run on the server.
 *
 * @since 0.5.0
 */
public interface ScheduledTask {

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
}
