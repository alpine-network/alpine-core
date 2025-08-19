/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.interaction;

import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public class InteractContext {

    private ActionResult result;

    public InteractContext(@NotNull ActionResult result) {
        this.result = result;
    }

    /**
     * Retrieves the result of the interaction.
     *
     * @return the {@link ActionResult} of the action
     */
    public @NotNull ActionResult result() {
        return this.result;
    }

    /**
     * Sets the result of the interaction.
     *
     * @param result the {@link ActionResult} to set
     */
    public void result(@NotNull ActionResult result) {
        this.result = result;
    }
}
