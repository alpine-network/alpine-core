/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @since 0.4.10
 */
public final class MessageDispatcher {

    private final AlpinePlugin plugin;

    private final long rateLimitDuration;

    // Cache<Component/Player hash, expire time>
    private final Cache<Long, Long> rateLimited;

    private MessageDispatcher(@NotNull AlpinePlugin plugin, long rateLimitDuration) {
        this.plugin = plugin;
        this.rateLimitDuration = rateLimitDuration;

        if (this.rateLimitDuration > 0) {
            this.rateLimited = CacheBuilder.newBuilder()
                    .expireAfterWrite(this.rateLimitDuration, TimeUnit.MILLISECONDS)
                    .build();
        }
        else {
            this.rateLimited = null;
        }
    }

    /**
     * Sends a message to the specified CommandSender, applying rate-limiting if enabled.
     *
     * @param sender    the recipient of the message
     * @param component the message to send
     */
    public void send(@NotNull CommandSender sender, @NotNull Component component) {
        if (this.rateLimited != null) {
            try {
                long hash = Objects.hash(sender.getName().hashCode(), component.hashCode());
                long now = System.currentTimeMillis();

                // Check if the message was recently sent
                Long expireTime = this.rateLimited.getIfPresent(hash);
                if (expireTime != null && now < expireTime) {
                    return;
                }

                // Store a new expiration time
                this.rateLimited.put(hash, now + this.rateLimitDuration);
            }
            catch (Exception ex) {
                this.plugin.log("Unable to deliver rate-limited message to " + sender.getName(), ex);
            }
        }

        Messaging.send(sender, component);
    }

    /**
     * Sends a formatted message to a recipient.
     *
     * @param sender       the recipient of the message
     * @param relational   an optional secondary sender for relational placeholders
     * @param message      the configured message template
     * @param placeholders a map of placeholder keys and their replacements
     */
    public void send(
            @NotNull CommandSender sender,
            @Nullable CommandSender relational,
            @NotNull ConfigMessage message,
            @NotNull Map<String, Object> placeholders
    ) {
        if (sender instanceof OfflinePlayer) {
            OfflinePlayer relationalPlayer = relational instanceof OfflinePlayer ? (OfflinePlayer) relational : null;
            this.send(sender, message.build(this.plugin, (OfflinePlayer) sender,
                    relationalPlayer, placeholders));
        }
        else {
            this.send(sender, message.build(this.plugin, placeholders));
        }
    }

    /**
     * Sends a formatted message to a recipient.
     *
     * @param sender       the recipient of the message
     * @param relational   an optional secondary sender for relational placeholders
     * @param message      the configured message template
     * @param placeholders a map of placeholder keys and their replacements
     */
    public void send(
            @NotNull CommandSender sender,
            @Nullable CommandSender relational,
            @NotNull ConfigMessage message,
            @NotNull Object... placeholders
    ) {
        if (sender instanceof OfflinePlayer) {
            OfflinePlayer relationalPlayer = relational instanceof OfflinePlayer ? (OfflinePlayer) relational : null;
            this.send(sender, message.build(this.plugin, (OfflinePlayer) sender,
                    relationalPlayer, placeholders));
        }
        else {
            this.send(sender, message.build(this.plugin, placeholders));
        }
    }

    /**
     * Sends a formatted message to a recipient.
     *
     * @param sender       the recipient of the message
     * @param message      the configured message template
     * @param placeholders a map of placeholder keys and their replacements
     */
    public void send(
            @NotNull CommandSender sender,
            @NotNull ConfigMessage message,
            @NotNull Map<String, Object> placeholders
    ) {
        this.send(sender, message.build(this.plugin, placeholders));
    }

    /**
     * Sends a formatted message to a recipient.
     *
     * @param sender       the recipient of the message
     * @param message      the configured message template
     * @param placeholders a map of placeholder keys and their replacements
     */
    public void send(
            @NotNull CommandSender sender,
            @NotNull ConfigMessage message,
            @NotNull Object... placeholders
    ) {
        this.send(sender, message.build(this.plugin, placeholders));
    }

    /**
     * Creates a new MessageDispatcher instance with no rate-limiting.
     *
     * @param plugin the plugin instance
     * @return a new MessageDispatcher instance
     */
    public static @NotNull MessageDispatcher create(@NotNull AlpinePlugin plugin) {
        return new MessageDispatcher(plugin, -1);
    }

    /**
     * Creates a new MessageDispatcher instance with the specified rate-limit duration.
     *
     * @param plugin            the plugin instance
     * @param rateLimitDuration the duration for rate-limiting in milliseconds
     * @return a new MessageDispatcher instance
     */
    public static @NotNull MessageDispatcher create(@NotNull AlpinePlugin plugin, long rateLimitDuration) {
        return new MessageDispatcher(plugin, rateLimitDuration);
    }

    /**
     * Creates a new MessageDispatcher instance with the specified rate-limit duration.
     *
     * @param plugin            the plugin instance
     * @param rateLimitDuration the duration for rate-limiting
     * @param unit              the unit of time for the duration
     * @return a new MessageDispatcher instance
     */
    public static @NotNull MessageDispatcher create(@NotNull AlpinePlugin plugin, long rateLimitDuration,
                                                    @NotNull TimeUnit unit) {
        return new MessageDispatcher(plugin, unit.toMillis(rateLimitDuration));
    }
}
