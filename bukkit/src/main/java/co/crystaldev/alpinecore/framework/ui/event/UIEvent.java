/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.event;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Represents a user interface event.
 *
 * @since 0.4.0
 */
@Getter
public abstract class UIEvent {

    private final List<Integer> affectedSlots;

    public UIEvent(@NotNull Iterable<Integer> affectedSlots) {
        this.affectedSlots = ImmutableList.copyOf(affectedSlots);
    }

    public UIEvent(int slot) {
        this.affectedSlots = Collections.singletonList(slot);
    }

    public UIEvent() {
        this.affectedSlots = Collections.emptyList();
    }

    /**
     * Checks if the event affected any slots.
     *
     * @return true if the event affected any slots, false otherwise.
     * @since 0.4.0
     */
    public boolean affectsSlots() {
        return !this.affectedSlots.isEmpty();
    }

    /**
     * Checks if this UI event affected the given slot.
     *
     * @param slot The slot to check.
     * @return true if the slot is affected by this UI event, false otherwise.
     */
    public boolean affectedSlot(int slot) {
        return this.affectedSlots.contains(slot);
    }
}
