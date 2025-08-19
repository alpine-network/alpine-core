/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.storage;

import co.crystaldev.alpinecore.util.UuidTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Defines a contract for an object that performs
 * serialization and deserialization between an
 * advanced object and a simple "key" object.
 *
 * @param <T1> The type to be serialized from and deserialized to
 * @param <T2> The type to be serialized to and deserialized from
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public interface KeySerializer<T1, T2> {
    /**
     * Serializes the "advanced" object into
     * a "simple" key type.
     *
     * @param input The advanced object
     * @return The key representation
     */
    T2 serialize(T1 input);

    /**
     * Deserializes the "simple" key object into
     * an "advanced" object type.
     *
     * @param input The key object
     * @return The advanced representation
     */
    T1 deserialize(T2 input);

    /**
     * Serializer for wrapped number primitives
     *
     * @since 0.1.0
     */
    final class NumberKey implements KeySerializer<Number, Long> {
        @Override
        public Long serialize(Number input) {
            return input.longValue();
        }

        @Override
        public Number deserialize(Long input) {
            return input;
        }
    }

    /**
     * Serializer for strings
     *
     * @since 0.1.0
     */
    final class StringKey implements KeySerializer<String, String> {
        @Override
        public String serialize(String input) {
            return input;
        }

        @Override
        public String deserialize(String input) {
            return input;
        }
    }

    /**
     * Serializer for UUIDs
     *
     * @since 0.1.0
     */
    final class UuidKey implements KeySerializer<UUID, String> {
        @Override
        public String serialize(UUID input) {
            return UuidTypeAdapter.fromUUID(input);
        }

        @Override
        public UUID deserialize(String input) {
            return UuidTypeAdapter.fromString(input);
        }
    }

    /**
     * Serializer for player types
     *
     * @since 0.1.0
     */
    final class PlayerKey implements KeySerializer<OfflinePlayer, String> {
        @Override
        public String serialize(OfflinePlayer input) {
            return UuidTypeAdapter.fromUUID(input.getUniqueId());
        }

        @Override
        public OfflinePlayer deserialize(String input) {
            UUID id = UuidTypeAdapter.fromString(input);
            return Bukkit.getServer().getOfflinePlayer(id);
        }
    }
}
