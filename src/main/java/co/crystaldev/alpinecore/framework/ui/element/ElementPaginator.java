package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.type.PaginatorElement;
import co.crystaldev.alpinecore.framework.ui.element.type.PaginatorNavElement;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The ElementPaginator class is responsible for paginating elements
 * and building navigation elements for a user interface.
 *
 * @param <T> the type of the entries in the paginator
 * @since 0.4.0
 */
public final class ElementPaginator<T> {

    private final ElementProvider<T, ?> elementProvider;

    private final Function<UIContext, ItemStack> emptySlotProvider;

    private final Map<UIContext, PaginatorState> states = new HashMap<>();

    private ElementPaginator(@NotNull ElementProvider<T, ?> elementProvider,
                             @Nullable Function<UIContext, ItemStack> emptySlotProvider) {
        this.elementProvider = elementProvider;
        this.emptySlotProvider = emptySlotProvider;
    }

    /**
     * Retrieves or creates the paginator state for the given UI context.
     *
     * @param context the UI context
     * @return the existing or newly created {@link PaginatorState} for the given context
     */
    public @NotNull PaginatorState getOrCreateState(@NotNull UIContext context) {
        return this.states.computeIfAbsent(context,
                ctx -> new PaginatorState(this.elementProvider.size()));
    }

    /**
     * Initializes the paginator state with the specified page and page size.
     *
     * @param context  the UI context
     * @param page     the initial page to set (zero-based)
     * @param pageSize the number of elements per page
     */
    public void initPage(@NotNull UIContext context, int page, int pageSize) {
        PaginatorState state = this.getOrCreateState(context);
        state.setPageSize(pageSize);
        state.setPage(page);
    }

    /**
     * Retrieves the element provider for the UI paginator.
     *
     * @return The element provider
     */
    public @NotNull ElementProvider<T, ?> getProvider() {
        return this.elementProvider;
    }

    /**
     * Retrieves the next element from the paginator.
     *
     * @param context the UI context
     * @return the next element from the paginator
     */
    public @NotNull PaginatorElement<T> buildNextSlot(@NotNull UIContext context) {
        PaginatorState state = this.getOrCreateState(context);
        this.elementProvider.nextElement(context);

        int offset = this.elementProvider.getIndex(context) - 1;
        state.setPageSize(state.getPageSize() + 1);

        return new PaginatorElement<>(context, this.elementProvider, this.emptySlotProvider, state, offset);
    }

    /**
     * Builds a previous navigation element for the UI paginator.
     *
     * @param context   the UI context
     * @param item      the defined config item
     * @param emptyItem the item to display when no page is available
     * @return the previous navigation element
     */
    public @NotNull Element buildPreviousNav(@NotNull UIContext context, @NotNull DefinedConfigItem item,
                                             @Nullable DefinedConfigItem emptyItem) {
        PaginatorState state = this.getOrCreateState(context);
        return new PaginatorNavElement(context, state, -1, item, emptyItem);
    }

    /**
     * Builds a previous navigation element for the UI paginator.
     *
     * @param context the UI context
     * @param item    the defined config item
     * @return the previous navigation element
     */
    public @NotNull Element buildPreviousNav(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        return this.buildPreviousNav(context, item, null);
    }

    /**
     * Builds the next navigation element for the UI paginator.
     *
     * @param context   the UI context
     * @param item      the defined config item
     * @param emptyItem the item to display when no page is available
     * @return the next navigation element
     */
    public @NotNull Element buildNextNav(@NotNull UIContext context, @NotNull DefinedConfigItem item,
                                         @Nullable DefinedConfigItem emptyItem) {
        PaginatorState state = this.getOrCreateState(context);
        return new PaginatorNavElement(context, state, 1, item, emptyItem);
    }

    /**
     * Builds the next navigation element for the UI paginator.
     *
     * @param context the UI context
     * @param item    the defined config item
     * @return the next navigation element
     */
    public @NotNull Element buildNextNav(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        return this.buildNextNav(context, item, null);
    }

    /**
     * Builds a navigation info element for the UI paginator.
     *
     * @param context the UI context
     * @param item    the defined config item
     * @return the navigation info element
     */
    public @NotNull Element buildNavInfo(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        PaginatorState state = this.getOrCreateState(context);
        return new PaginatorNavElement(context, state, 0, item, null);
    }

    /**
     * Removes any stale states or states associated with a specific player from the context.
     *
     * @param context the UI context
     */
    public void closed(@NotNull UIContext context) {
        this.closed(context, true);
    }

    /**
     * Removes any stale states or states associated with a specific player from the context.
     *
     * @param context    the UI context
     * @param clearState whether to clear the paginator state for the context.
     */
    public void closed(@NotNull UIContext context, boolean clearState) {
        this.elementProvider.closed(context);

        if (clearState) {
            PaginatorState state = this.states.get(context);
            if (state != null) {
                state.resetPageSize();
            }
            this.states.entrySet().removeIf(e -> e.getKey().isStale());
        }
    }

    public static <S> @NotNull ElementPaginator<S> from(@NotNull ElementProvider<S, ?> elementProvider) {
        return new ElementPaginator<>(elementProvider, null);
    }

    public static <T> @NotNull Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * The Builder class provides a way to construct an instance of ElementPaginator class.
     * It allows setting the element provider and empty slot provider for the paginator.
     *
     * @param <T> the type of the entries in the paginator
     * @since 0.4.0
     */
    public static final class Builder<T> {

        private ElementProvider<T, ?> elementProvider;

        private Function<UIContext, ItemStack> emptySlotProvider;

        public @NotNull Builder<T> elementProvider(@NotNull ElementProvider<T, ?> elementProvider) {
            this.elementProvider = elementProvider;
            return this;
        }

        public @NotNull Builder<T> emptySlotProvider(@Nullable Function<UIContext, ItemStack> emptySlotProvider) {
            this.emptySlotProvider = emptySlotProvider;
            return this;
        }

        public @NotNull ElementPaginator<T> build() {
            Validate.notNull(this.elementProvider, "elementProvider cannot be null");
            return new ElementPaginator<>(this.elementProvider, this.emptySlotProvider);
        }
    }
}
