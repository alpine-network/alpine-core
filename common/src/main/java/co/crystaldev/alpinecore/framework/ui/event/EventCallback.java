/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.event;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event callback that handles UI events.
 *
 * @since 0.4.0
 */
@FunctionalInterface
public interface EventCallback<T extends UIEvent> {
    /**
     * Invokes the event callback method for the given UIContext and UIEvent.
     *
     * @param context the UIContext object representing the state of the user interface
     * @param event   the UIEvent object representing the user interface event
     * @return the ActionResult object representing the result of the event callback method
     */
    @NotNull
    ActionResult invoke(@NotNull UIContext context, @NotNull T event);
}
