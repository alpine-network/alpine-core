package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.type.PaginatorElement;
import co.crystaldev.alpinecore.framework.ui.element.type.PaginatorNavigationElement;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * The ElementPaginator class is responsible for paginating elements
 * and building navigation elements for a user interface.
 *
 * @param <T> the type of the entries in the paginator
 *
 * @since 0.4.0
 */
public final class ElementPaginator<T> {

    private final ElementProvider<T, ?> elementProvider;

    private final Function<UIContext, ItemStack> emptySlotProvider;

    private final Map<UIContext, State> states = new HashMap<>();

    private ElementPaginator(@NotNull ElementProvider<T, ?> elementProvider,
                             @Nullable Function<UIContext, ItemStack> emptySlotProvider) {
        this.elementProvider = elementProvider;
        this.emptySlotProvider = emptySlotProvider;
    }

    /**
     * Retrieves the next element from the paginator.
     *
     * @param context the UI context
     * @return the next element from the paginator
     */
    @NotNull
    public PaginatorElement<T> buildNextSlot(@NotNull UIContext context) {
        State state = this.states.computeIfAbsent(context,
                ctx -> new State(this.elementProvider.size()));
        this.elementProvider.nextElement(context);

        int offset = this.elementProvider.getIndex(context) - 1;
        state.setPageSize(state.pageSize + 1);

        return new PaginatorElement<>(context, this.elementProvider, this.emptySlotProvider, state, offset);
    }

    /**
     * Builds a previous navigation element for the UI paginator.
     *
     * @param context the UI context
     * @param item the defined config item
     * @return the previous navigation element
     */
    @NotNull
    public Element buildPreviousNav(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        State state = this.states.computeIfAbsent(context,
                ctx -> new State(this.elementProvider.getEntries().size()));
        PaginatorNavigationElement element = new PaginatorNavigationElement(context, state, item);
        element.setOnClick((ctx, click) -> {
            state.setPage(state.getCurrentPage() - 1);
            context.refresh();
        });
        return element;
    }

    /**
     * Builds the next navigation element for the UI paginator.
     *
     * @param context the UI context
     * @param item the defined config item
     * @return the next navigation element
     */
    @NotNull
    public Element buildNextNav(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        State state = this.states.computeIfAbsent(context,
                ctx -> new State(this.elementProvider.getEntries().size()));
        PaginatorNavigationElement element = new PaginatorNavigationElement(context, state, item);
        element.setOnClick((ctx, click) -> {
            state.setPage(state.getCurrentPage() + 1);
            context.refresh();
        });
        return element;
    }

    /**
     * Builds a navigation info element for the UI paginator.
     *
     * @param context the UI context
     * @param item    the defined config item
     * @return the navigation info element
     */
    @NotNull
    public Element buildNavInfo(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        State state = this.states.computeIfAbsent(context,
                ctx -> new State(this.elementProvider.getEntries().size()));
        return new PaginatorNavigationElement(context, state, item);
    }

    /**
     * Removes any stale states or states associated with a specific player from the context.
     *
     * @param context the UI context
     */
    public void closed(@NotNull UIContext context) {
        this.elementProvider.closed(context);
        this.states.entrySet().removeIf(e -> e.getKey().isStale() || e.getKey().playerId().equals(context.playerId()));
    }

    @NotNull
    public static <S> ElementPaginator<S> from(@NotNull ElementProvider<S, ?> elementProvider) {
        return new ElementPaginator<>(elementProvider, null);
    }

    @NotNull
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    @Getter
    public static final class State {

        private final int elementCount;
        private int pageSize;
        private int maxPages;
        private int currentPage; // zero-based

        private State(int elementCount) {
            this.elementCount = elementCount;
        }

        private void setPageSize(int pageSize) {
            this.pageSize = pageSize;
            this.maxPages = (int) Math.ceil(this.elementCount / (double) pageSize);
        }

        public void setPage(int page) {
            this.currentPage = Math.max(0, Math.min(this.maxPages - 1, page));
        }
    }

    /**
     * The Builder class provides a way to construct an instance of ElementPaginator class.
     * It allows setting the element provider and empty slot provider for the paginator.
     *
     * @param <T> the type of the entries in the paginator
     *
     * @since 0.4.0
     */
    public static final class Builder<T> {

        private ElementProvider<T, ?> elementProvider;

        private Function<UIContext, ItemStack> emptySlotProvider;

        @NotNull
        public Builder<T> elementProvider(@NotNull ElementProvider<T, ?> elementProvider) {
            this.elementProvider = elementProvider;
            return this;
        }

        @NotNull
        public Builder<T> emptySlotProvider(@Nullable Function<UIContext, ItemStack> emptySlotProvider) {
            this.emptySlotProvider = emptySlotProvider;
            return this;
        }

        @NotNull
        public ElementPaginator<T> build() {
            Validate.notNull(this.elementProvider, "elementProvider cannot be null");
            return new ElementPaginator<>(this.elementProvider, this.emptySlotProvider);
        }
    }
}
