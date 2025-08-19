/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.event;

import lombok.AllArgsConstructor;

/**
 * @since 0.4.0
 */
@AllArgsConstructor
final class RegisteredEventCallback<T extends UIEvent> {
    final Object source;
    final EventCallback<T> callback;
    final byte priority;
}
