/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

/**
 * A basic timer that measures time in milliseconds.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class SimpleTimer {
    /** Timer start time in milliseconds since Unix epoch */
    private long startMillis = 0L;

    /**
     * Start the timer.
     */
    public void start() {
        this.startMillis = System.currentTimeMillis();
    }

    /**
     * Stop the timer.
     *
     * @return The elapsed milliseconds
     */
    public long stop() {
        if (startMillis > 0L) {
            return System.currentTimeMillis() - this.startMillis;
        }
        else {
            throw new IllegalStateException("Attempted to stop inactive SimpleTimer");
        }
    }
}
