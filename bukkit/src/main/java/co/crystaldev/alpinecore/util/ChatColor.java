/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility for interacting with text utilizing the
 * legacy Minecraft chat code system.
 *
 * @see <a href="https://wiki.vg/Chat#Old_system">Chat - Old system</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@Getter
public enum ChatColor {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    public static final ImmutableMap<String, String> CHAT_COLOR_TO_ANSI = ImmutableMap.<String, String>builder()
            .put("§0", "\u001b[30m")
            .put("§1", "\u001b[34m")
            .put("§2", "\u001b[32m")
            .put("§3", "\u001b[36m")
            .put("§4", "\u001b[31m")
            .put("§5", "\u001b[35m")
            .put("§6", "\u001b[33m")
            .put("§7", "\u001b[37m")
            .put("§8", "\u001b[90m")
            .put("§9", "\u001b[94m")
            .put("§a", "\u001b[92m")
            .put("§b", "\u001b[96m")
            .put("§c", "\u001b[91m")
            .put("§d", "\u001b[95m")
            .put("§e", "\u001b[93m")
            .put("§f", "\u001b[97m")
            .put("§l", "\u001b[1m")
            .put("§o", "\u001b[3m")
            .put("§n", "\u001b[4m")
            .put("§m", "\u001b[9m")
            .put("§k", "\u001b[6m")
            .put("§r", "\u001b[0m")
            .build();

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + "§" + "[\\dA-FK-OR]");
    private static final Map<Character, ChatColor> BY_CHAR = new HashMap<>();

    /** The character code */
    private final char code;
    /** Whether the code is a formatting code */
    private final boolean format;

    static {
        for (ChatColor color : values()) {
            BY_CHAR.put(color.code, color);
        }
    }

    ChatColor(char code) {
        this(code, false);
    }

    ChatColor(char code, boolean format) {
        this.code = code;
        this.format = format;
    }

    /**
     * Check if a given code is a color code.
     *
     * @return True if the code represents a color
     */
    public boolean isColor() {
        return !this.format && this != RESET;
    }

    /**
     * Resolve the enum value for a given code.
     *
     * @param code The code
     * @return The enum value, or null if not found
     */
    public static @Nullable ChatColor getByChar(char code) {
        return BY_CHAR.get(code);
    }

    /**
     * Resolve the enum value for a given code.
     *
     * @param code The code, of which the first character will be used
     * @return The enum value, or null if not found
     */
    public static @Nullable ChatColor getByChar(@NotNull String code) {
        return BY_CHAR.get(code.charAt(0));
    }

    /**
     * Strip color codes from a message.
     *
     * @param input The message
     * @return The stripped message
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable String stripColor(@Nullable String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    /**
     * Translates a message that uses ampersands to denote format
     * into a Minecraft compatible formatted message.
     *
     * @param input The text to be translated
     * @return The Minecraft formatted message
     */
    public static @NotNull String translate(@NotNull String input) {
        return translate(input, '&');
    }

    /**
     * Translates a message using a generic character to
     * denote format into a Minecraft compatible formatted
     * message.
     *
     * @param input      The text to be translated
     * @param formatChar The character used to denote formatting
     * @return The Minecraft formatted message
     */
    public static @NotNull String translate(@NotNull String input, char formatChar) {
        char[] b = input.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == formatChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    /**
     * Translates a Minecraft compatible formatted message
     * into an ANSI escape code compatible formatted message.
     *
     * @param input The text to be translated
     * @return The ANSI escape code formatted message
     */
    public static @NotNull String translateToAnsi(@NotNull String input) {
        return translateToAnsi(input, false);
    }

    /**
     * Translates a Minecraft compatible formatted message
     * into an ANSI escape code compatible formatted message.
     *
     * @param input  The text to be translated
     * @param format If the message should be Minecraft formatted beforehand
     * @return The ANSI escape code formatted message
     */
    public static @NotNull String translateToAnsi(@NotNull String input, boolean format) {
        if (format)
            input = translate(input);

        for (Map.Entry<String, String> entry : CHAT_COLOR_TO_ANSI.entrySet())
            input = input.replaceAll(entry.getKey(), entry.getValue());

        return input;
    }

    @Override
    public String toString() {
        return "§" + this.code;
    }
}
