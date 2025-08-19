/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An {@link Event} implementation that handles the usual
 * {@link HandlerList} boilerplate.
 *
 * @see <a href="https://www.spigotmc.org/wiki/using-the-event-api/">Using the Event API</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public AlpineEvent() {
        super(false);
    }

    public AlpineEvent(boolean async) {
        super(async);
    }

    @Override
    public final HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
