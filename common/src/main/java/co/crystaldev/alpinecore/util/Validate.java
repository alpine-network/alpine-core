/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Argument validation, replacing {@code org.apache.commons.lang.Validate}.
 * <p>
 * Commons Lang 2 was never a declared dependency - it was simply on the classpath because Spigot
 * provided it. Modern Paper does not, so a framework that reaches for it is a framework that
 * cannot run on Paper.
 *
 * @since 0.5.0
 */
@ApiStatus.Internal
@UtilityClass
public final class Validate {

    private static final String DEFAULT_IS_NULL_EX_MESSAGE = "The validated object is null";

    private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "The validated expression is false";

    /**
     * @param object the object to check
     * @throws IllegalArgumentException if the object is {@code null}
     */
    @Contract("null -> fail")
    public static void notNull(@Nullable Object object) {
        notNull(object, DEFAULT_IS_NULL_EX_MESSAGE);
    }

    /**
     * @param object  the object to check
     * @param message the exception message
     * @throws IllegalArgumentException if the object is {@code null}
     */
    @Contract("null, _ -> fail")
    public static void notNull(@Nullable Object object, @NotNull String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * @param expression the expression to check
     * @throws IllegalArgumentException if the expression is {@code false}
     */
    @Contract("false -> fail")
    public static void isTrue(boolean expression) {
        isTrue(expression, DEFAULT_IS_TRUE_EX_MESSAGE);
    }

    /**
     * @param expression the expression to check
     * @param message    the exception message
     * @throws IllegalArgumentException if the expression is {@code false}
     */
    @Contract("false, _ -> fail")
    public static void isTrue(boolean expression, @NotNull String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
}
