/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.interaction;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@FunctionalInterface
public interface ClickFunction {
    /**
     * Called when a mouse button is clicked.
     *
     * @param context the ui context for the interaction
     * @param click   the click context for the interaction
     */
    void mouseClicked(@NotNull UIContext context, @NotNull ClickContext click);
}
