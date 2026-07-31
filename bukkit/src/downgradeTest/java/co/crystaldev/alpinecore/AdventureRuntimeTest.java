/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Ensure we do not break Adventure behavior during our Java 8 downgrade.
 *
 * @since 0.5.0
 */
public class AdventureRuntimeTest {

    @Test
    public void miniMessageRoundTrips() {
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize("<red>hello <bold>world</bold></red>");

        assertEquals(PlainTextComponentSerializer.plainText().serialize(parsed), "hello world");
        assertTrue(mm.serialize(parsed).contains("red"));
    }

    /** The exact path the Bukkit audience bridge takes for every chat message. */
    @Test
    public void legacySerializationMatchesTheBridge() {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.SECTION_CHAR)
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();

        assertEquals(serializer.serialize(Component.text("hi", NamedTextColor.GREEN)), "§ahi");

        // Hex must survive as the section-x encoding modern Spigot renders as true color.
        String hex = serializer.serialize(Component.text("x", TextColor.fromHexString("#ff8800")));
        assertTrue(hex.contains("§x"), "expected a section-x hex sequence, got: " + hex);
    }

    @Test
    public void joinAndEventsSurvive() {
        List<Component> parts = Arrays.asList(
                Component.text("a"), Component.text("b"), Component.text("c"));
        Component joined = Component.join(JoinConfiguration.separator(Component.text(", ")), parts);
        assertEquals(PlainTextComponentSerializer.plainText().serialize(joined), "a, b, c");

        Component withEvents = Component.text("click")
                .hoverEvent(HoverEvent.showText(Component.text("tooltip")))
                .clickEvent(ClickEvent.runCommand("/spawn"));
        assertNotNull(withEvents.hoverEvent());
        assertNotNull(withEvents.clickEvent());
    }

    /** Mirrors StyleTagResolver, the most API-exposed class in the codebase. */
    @Test
    public void customTagResolversWork() {
        TagResolver custom = TagResolver.resolver("shout",
                (args, ctx) -> Tag.selfClosingInserting(Component.text("HEY")));
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.resolver(TagResolver.standard(), custom))
                .build();

        Component parsed = mm.deserialize("say <shout> now");
        assertEquals(PlainTextComponentSerializer.plainText().serialize(parsed), "say HEY now");
    }

    /** Adventure 5 generates these from records; the downgrade strips ACC_RECORD. */
    @Test
    public void recordDerivedMethodsStillWork() {
        assertEquals(Component.text("q", NamedTextColor.RED), Component.text("q", NamedTextColor.RED));
        assertEquals(Component.text("q").hashCode(), Component.text("q").hashCode());
        assertNotNull(Component.text("q").toString());
    }
}
