/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Resolves the single {@link AlpinePlatform} bundled with this AlpineCore distribution.
 *
 * @since 0.5.0
 */
public final class Platform {

    private static volatile AlpinePlatform instance;

    private Platform() {
    }

    public static @NotNull AlpinePlatform get() {
        AlpinePlatform local = instance;
        return local == null ? bind() : local;
    }

    /**
     * @return whether the platform has already been resolved
     */
    public static boolean isBound() {
        return instance != null;
    }

    @ApiStatus.Internal
    public static synchronized @NotNull AlpinePlatform bind() {
        AlpinePlatform local = instance;
        if (local != null) {
            return local;
        }
        return bindWith(Platform.class.getClassLoader());
    }

    @ApiStatus.Internal
    static synchronized @NotNull AlpinePlatform bindWith(@NotNull ClassLoader loader) {
        AlpinePlatform local = instance;
        if (local != null) {
            return local;
        }

        List<AlpinePlatform> found = new ArrayList<>(2);
        for (AlpinePlatform platform : ServiceLoader.load(AlpinePlatform.class, loader)) {
            found.add(platform);
        }

        if (found.isEmpty()) {
            throw new IllegalStateException("No AlpinePlatform provider is present on the classpath. "
                    + "This AlpineCore build is malformed - please report it at "
                    + "https://github.com/alpine-network/alpine-core/issues");
        }
        if (found.size() > 1) {
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < found.size(); i++) {
                if (i > 0) {
                    ids.append(", ");
                }
                ids.append(found.get(i).getClass().getName());
            }
            throw new IllegalStateException("Found " + found.size() + " AlpinePlatform providers [" + ids + "]. " +
                    "Exactly one AlpineCore distribution may be installed - remove the extras.");
        }

        instance = found.get(0);
        return instance;
    }

    @ApiStatus.Internal
    static synchronized void reset() {
        instance = null;
    }
}
