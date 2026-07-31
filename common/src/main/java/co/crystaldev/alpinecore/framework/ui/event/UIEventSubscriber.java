/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.event;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a class that subscribes to UI events and registers them to a UI event bus.
 *
 * @since 0.4.0
 */
public interface UIEventSubscriber {
    /**
     * Registers the events of a UIContext to a UIEventBus.
     *
     * @param bus the UIEventBus object to register the events to
     */
    void registerEvents(@NotNull UIEventBus bus);
}
