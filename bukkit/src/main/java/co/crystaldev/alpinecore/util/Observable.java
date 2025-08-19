/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A container that allows observing changes to its value.
 *
 * @since 0.4.10
 */
public class Observable<T> {

    protected T value;

    protected final Map<Object, Object> observers;

    protected Observable(T initial) {
        this.value = initial;
        this.observers = new HashMap<>();
    }

    /**
     * Creates a new observable container with the given initial value.
     *
     * @param initial the initial value to store
     * @return a new observable container
     */
    public static <T> @NotNull Observable<T> of(@Nullable T initial) {
        return new Observable<>(initial);
    }

    /**
     * Gets the current value stored in this container.
     *
     * @return the current value
     */
    public T get() {
        return this.value;
    }

    /**
     * Changes the value stored in this container.
     * Observers are notified only if the value changes.
     *
     * @param newValue the new value to store
     */
    public void set(@Nullable T newValue) {
        this.set(newValue, false);
    }

    /**
     * Changes the value stored in this container.
     * Observers are notified only if the value changes,
     * unless forced.
     *
     * @param newValue    the new value to store
     * @param forceNotify whether to force an update
     */
    public void set(@Nullable T newValue, boolean forceNotify) {
        T oldValue = this.value;
        this.value = newValue;

        if (forceNotify || !Objects.equals(this.value, oldValue)) {
            this.notifyObservers(newValue, oldValue);
        }
    }

    /**
     * Adds an observer to be notified when the value changes.
     *
     * @param key      the key to identify the observer
     * @param observer the function to run when the value changes
     */
    public void observe(@NotNull Object key, @NotNull Consumer<T> observer) {
        this.observers.put(key, observer);
    }

    /**
     * Adds an observer to be notified when the value changes.
     *
     * @param key      the key to identify the observer
     * @param observer the function to run when the value changes,
     *                 providing both the old and new values
     */
    public void observe(@NotNull Object key, @NotNull BiConsumer<T, T> observer) {
        this.observers.put(key, observer);
    }

    /**
     * Removes an observer.
     *
     * @param key the key identifying the observer to remove
     */
    public void removeObserver(@NotNull Object key) {
        this.observers.remove(key);
    }

    /**
     * Notifies all observers of the current value.
     */
    public void notifyObservers() {
        this.notifyObservers(this.value, this.value);
    }

    protected void notifyObservers(T value, T oldValue) {
        for (Object observer : this.observers.values()) {
            if (observer instanceof Consumer) {
                ((Consumer) observer).accept(value);
            }
            else if (observer instanceof BiConsumer) {
                ((BiConsumer) observer).accept(value, oldValue);
            }
        }
    }
}
