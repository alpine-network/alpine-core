/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform.bukkit;

import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * @since 0.5.0
 */
final class BukkitSenderAudience implements Audience {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private final CommandSender sender;

    BukkitSenderAudience(@NotNull CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        this.sender.sendMessage(serialize(message));
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        if (this.sender instanceof Player) {
            ActionBar.sendActionBar((Player) this.sender, serialize(message));
        }
        else {
            // Consoles have no action bar; fall back to chat rather than dropping the message.
            this.sendMessage(message);
        }
    }

    @Override
    public void showTitle(@NotNull Title title) {
        if (!(this.sender instanceof Player)) {
            this.sendMessage(title.title());
            this.sendMessage(title.subtitle());
            return;
        }

        Title.Times times = title.times();
        Titles.sendTitle(
                (Player) this.sender,
                ticks(times == null ? null : times.fadeIn(), 10),
                ticks(times == null ? null : times.stay(), 70),
                ticks(times == null ? null : times.fadeOut(), 20),
                serialize(title.title()),
                serialize(title.subtitle())
        );
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        if (!(this.sender instanceof Player)) {
            return;
        }

        Player player = (Player) this.sender;
        if (part == TitlePart.TITLE) {
            Titles.sendTitle(player, serialize((Component) value), "");
        }
        else if (part == TitlePart.SUBTITLE) {
            Titles.sendTitle(player, "", serialize((Component) value));
        }
        // TitlePart.TIMES has no standalone XSeries equivalent; showTitle carries the timings.
    }

    @Override
    public void clearTitle() {
        if (this.sender instanceof Player) {
            Titles.clearTitle((Player) this.sender);
        }
    }

    @Override
    public void resetTitle() {
        this.clearTitle();
    }

    private static @NotNull String serialize(@NotNull Component component) {
        return SERIALIZER.serialize(component);
    }

    /** Converts a duration to ticks, falling back to the vanilla default when unset. */
    private static int ticks(Duration duration, int fallback) {
        if (duration == null || duration.isNegative()) {
            return fallback;
        }
        return (int) (duration.toMillis() / 50L);
    }
}
