/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.util.Observable;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents the state of a paginated UI element.
 *
 * @since 0.4.10
 */
@Getter
public final class PaginatorState {

    private int elementCount;

    private int pageSize;

    private int maxPages;

    private final Observable<Integer> currentPage; // zero-based

    /**
     * Creates a paginator with the specified element count and defaults to page 0.
     *
     * @param elementCount the total number of elements
     */
    public PaginatorState(int elementCount) {
        this.elementCount = elementCount;
        this.currentPage = Observable.of(0);
    }

    /**
     * Creates a paginator with the specified element count, initial page, and page size.
     *
     * @param elementCount the total number of elements
     * @param page         the initial page (zero-based)
     * @param pageSize     the number of elements per page
     */
    public PaginatorState(int elementCount, int page, int pageSize) {
        this.elementCount = elementCount;
        this.setPageSize(pageSize);
        this.currentPage = Observable.of(Math.max(0, Math.min(this.maxPages - 1, page)));
    }

    /**
     * Sets the number of elements contained by the paginator.
     *
     * @param elementCount the number of elements.
     */
    public void setElementCount(int elementCount) {
        this.elementCount = elementCount;
    }

    /**
     * Sets the page size and recalculates the maximum number of pages.
     *
     * @param pageSize the number of elements per page
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.maxPages = pageSize == 0 ? 0 : (int) Math.ceil(this.elementCount / (double) pageSize);
    }

    /**
     * Resets the page size and maximum pages to zero.
     */
    public void resetPageSize() {
        this.pageSize = 0;
        this.maxPages = 0;
    }

    /**
     * Sets the current page to the specified value, clamped within valid bounds.
     *
     * @param page the page to set (zero-based)
     */
    public void setPage(int page) {
        this.currentPage.set(Math.max(0, Math.min(this.maxPages - 1, page)));
    }

    /**
     * Checks if the specified page number is valid.
     *
     * @param page the page to check
     * @return true if the page is valid, false otherwise
     */
    public boolean isValid(int page) {
        return page >= 0 && page <= this.maxPages - 1;
    }

    /**
     * Returns an observable for the current page.
     *
     * @return the current page observable
     */
    public @NonNull Observable<Integer> pageObservable() {
        return this.currentPage;
    }

    /**
     * Gets the current page number.
     *
     * @return the current page (zero-based)
     */
    public int getCurrentPage() {
        return this.currentPage.get();
    }
}
