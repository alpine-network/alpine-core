/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * An {@link Event} implementation that handles the usual
 * {@link Cancellable} boilerplate.
 *
 * @see AlpineEvent
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineCancellableEvent extends AlpineEvent implements Cancellable {
    protected boolean cancelled = false;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
