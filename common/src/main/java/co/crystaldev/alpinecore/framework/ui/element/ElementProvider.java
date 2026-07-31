/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * The ElementProvider class provides a way to iterate over a collection of objects and
 * convert them into elements using a provided function.
 *
 * @param <S> the type of the entries in the provider
 * @param <T> the type of the elements built by the provider
 *
 * @since 0.4.0
 */
public final class ElementProvider<S, T extends Element> {

    @Getter
    private final @NotNull Collection<S> entries;

    private final BiFunction<UIContext, S, T> toElementFunction;

    private final Map<UIContext, State<S>> states = new HashMap<>();

    private ElementProvider(@NotNull Collection<S> entries, @NotNull BiFunction<UIContext, S, T> toElementFunction) {
        this.entries = entries;
        this.toElementFunction = toElementFunction;
    }

    /**
     * Retrieves the next element from the paginator.
     *
     * @param context the UI context
     * @return the next element from the paginator
     */
    public @Nullable T nextElement(@NotNull UIContext context) {
        State<S> state = this.states.computeIfAbsent(context, ctx -> new State<>(this.entries.iterator()));
        S next = state.next();
        return next != null ? this.toElementFunction.apply(context, next) : null;
    }

    /**
     * Skips to the specified index in the iterator and retrieves the element at that position.
     *
     * @param context the UI context
     * @param index   the index to skip to (0-based)
     * @return the element at the specified index
     */
    public @Nullable T skipToIndex(@NotNull UIContext context, int index) {
        Validate.isTrue(index >= 0 && index < this.entries.size(), "index out of bounds");

        State<S> state = new State<>(this.entries.iterator());
        this.states.put(context, state);

        S next = null;
        while (index-- > 0 && (next = state.next()) != null) {
        }

        return next != null ? this.toElementFunction.apply(context, next) : null;
    }

    /**
     * Retrieves an element from the paginator at the specified index.
     *
     * @param context the UI context
     * @param index the index of the element to retrieve
     * @return the element at the specified index
     */
    public @NotNull T getElement(@NotNull UIContext context, int index) {
        Validate.isTrue(index >= 0 && index < this.entries.size(), "index out of bounds");
        return this.toElementFunction.apply(context, this.entries.stream().skip(index).findFirst().orElse(null));
    }

    /**
     * Retrieves the index of an element from the iterator in the given UI context.
     *
     * @param context The UI context.
     * @return The index of the element at the current UI context, or -1 if the element is not found.
     */
    public int getIndex(@NotNull UIContext context) {
        State<S> state = this.states.get(context);
        return state == null ? -1 : state.index;
    }

    /**
     * Retrieves the size of the entries in the provider.
     *
     * @return the number of entries
     */
    public int size() {
        return this.entries.size();
    }

    /**
     * Removes any stale states or states associated with a specific player from the context.
     *
     * @param context the UI context
     */
    public void closed(@NotNull UIContext context) {
        this.states.entrySet().removeIf(e -> e.getKey().isStale() || e.getKey().playerId().equals(context.playerId()));
    }

    public static <S, T extends Element> @NotNull Builder<S, T> builder() {
        return new Builder<>();
    }

    @RequiredArgsConstructor
    private static final class State<S> {
        private final Iterator<S> iterator;
        private int index;

        public @Nullable S next() {
            this.index++;
            return this.iterator.hasNext() ? this.iterator.next() : null;
        }
    }

    /**
     * The Builder class is used to construct an ElementProvider object.
     *
     * @param <S> the type of the entries in the provider
     * @param <T> the type of the elements built by the provider
     *
     * @since 0.4.0
     */
    public static final class Builder<S, T extends Element> {
        private Collection<S> entries;
        private BiFunction<UIContext, S, T> toElementFunction;

        public @NotNull Builder<S, T> entries(@NotNull Collection<S> entries) {
            Validate.notNull(entries, "entries cannot be null");
            this.entries = entries;
            return this;
        }

        public @NotNull Builder<S, T> entries(@NotNull S... entries) {
            Validate.notNull(entries, "entries cannot be null");
            this.entries = ImmutableList.copyOf(entries);
            return this;
        }

        public @NotNull Builder<S, T> element(@NotNull BiFunction<UIContext, S, T> toElementFunction) {
            Validate.notNull(toElementFunction, "toElementFunction cannot be null");
            this.toElementFunction = toElementFunction;
            return this;
        }

        public @NotNull ElementPaginator<S> createPaginator() {
            return ElementPaginator.from(this.build());
        }

        public @NotNull ElementProvider<S, T> build() {
            Validate.notNull(this.entries, "entries cannot be null");
            Validate.notNull(this.toElementFunction, "toElementFunction cannot be null");
            return new ElementProvider<>(this.entries, this.toElementFunction);
        }
    }
}
