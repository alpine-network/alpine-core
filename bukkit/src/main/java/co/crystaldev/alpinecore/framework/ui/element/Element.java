/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.ui.*;
import co.crystaldev.alpinecore.framework.ui.element.type.EmptyElement;
import co.crystaldev.alpinecore.framework.ui.element.type.GenericElement;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.event.UIEventSubscriber;
import co.crystaldev.alpinecore.framework.ui.interaction.ClickContext;
import co.crystaldev.alpinecore.framework.ui.interaction.ClickFunction;
import co.crystaldev.alpinecore.framework.ui.interaction.ClickProperties;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents an abstract UI Element.
 *
 * @since 0.4.0
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class Element implements UIEventSubscriber {

    protected final UIContext context;

    @Getter @Setter
    protected SlotPosition position;

    protected Map<String, Object> attributes;

    @Setter
    protected ClickFunction onClick;

    @Getter @Setter
    protected ClickProperties clickProperties = ClickProperties.ALL_DISALLOWED;

    public Element(@NotNull UIContext context) {
        this.context = context;
    }

    /**
     * Builds an ItemStack representing this UI element.
     *
     * @return the ItemStack representing this UI element, or null if there is no item stack
     */
    public abstract @Nullable ItemStack buildItemStack();

    /**
     * Should null/empty items be allowed to populate this element?
     *
     * @return Whether this element can provide a null element to the UIHandler.
     */
    public boolean allowsEmptyItems() {
        return true;
    }

    /**
     * Initializes the UI element.
     */
    public void init() {
        // NO OP
    }

    /**
     * Called when the UI has been closed.
     */
    public void closed() {
        // NO OP
    }

    /**
     * Called when the UI element is clicked.
     *
     * @param context the click context for the interaction
     */
    public void clicked(@NotNull ClickContext context) {
        if (this.onClick != null) {
            this.onClick.mouseClicked(this.context, context);
        }
    }

    /**
     * Retrieves the attribute with the specified key.
     *
     * @param <T> the type of the attribute value
     *
     * @param key the key of the attribute to retrieve
     * @return the attribute value associated with the key, or null if the attribute does not exist
     */
    public <T> @Nullable T getAttribute(@NotNull String key) {
        return this.attributes == null ? null : (T) this.attributes.get(key);
    }

    /**
     * Sets an attribute with the specified key and value for the Element.
     *
     * @param key   the key of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(@NotNull String key, @Nullable Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    /**
     * Retrieves the attributes associated with this Element.
     *
     * @return a map of attribute keys to attribute values
     */
    public @NotNull Map<String, Object> getAttributes() {
        return this.attributes == null ? Collections.emptyMap() : this.attributes;
    }

    /**
     * Sets the attributes with the specified key-value pairs for the Element.
     *
     * @param attributes a map of attribute keys to attribute values
     */
    public void putAttributes(@NotNull Map<String, Object> attributes) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.putAll(attributes);
    }

    @Override
    public void registerEvents(@NotNull UIEventBus bus) {
        // NO OP
    }

    public static boolean isEmpty(@Nullable Element element) {
        return element == null || element instanceof EmptyElement;
    }

    public static @NotNull GenericElement of(@NotNull UIContext context, @NotNull ItemStack itemStack) {
        return new GenericElement(context, itemStack);
    }

    public static @NotNull Element fromNullable(@Nullable Element element) {
        return element == null ? empty() : element;
    }

    public static @NotNull Element empty() {
        return EmptyElement.INSTANCE;
    }
}
