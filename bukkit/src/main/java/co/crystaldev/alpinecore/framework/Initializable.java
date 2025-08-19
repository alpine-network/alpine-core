/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework;

/**
 * Defines an interface for objects that require late initialization.
 *
 * @author BestBearr
 * @since 0.2.0
 */
public interface Initializable {

    /**
     * Initializes the object. Implementations should perform all necessary setup here.
     *
     * @return boolean Returns {@code true} if initialization is successful, {@code false} otherwise.
     */
    boolean init();
}
