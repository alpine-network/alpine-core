/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

/**
 * An adapter for utilizing non-dashed UUIDs in Gson.
 *
 * @author BestBearr
 * @since 0.1.0
 */
public final class UuidTypeAdapter extends TypeAdapter<UUID> {
    @Override
    public void write(JsonWriter out, UUID value) throws IOException {
        out.value(fromUUID(value));
    }

    @Override
    public UUID read(JsonReader in) throws IOException {
        return fromString(in.nextString());
    }

    /**
     * Converts a UUID object into a non-dashed string.
     *
     * @param value The UUID object
     * @return The non-dashed string
     */
    public static @NotNull String fromUUID(@NotNull UUID value) {
        return value.toString().replace("-", "");
    }

    /**
     * Converts a non-dashed string into a UUID object.
     *
     * @param input The non-dashed string
     * @return The UUID object
     */
    public static @NotNull UUID fromString(@NotNull String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
