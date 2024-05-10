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

    @NotNull
    public PaginatorElement<T> nextElement(@NotNull UIContext context) {
        State state = this.states.computeIfAbsent(context,
                ctx -> new State(this.elementProvider.size()));
        this.elementProvider.nextElement(context);

        int offset = this.elementProvider.getIndex(context) - 1;
        state.setPageSize(state.pageSize + 1);

        return new PaginatorElement<>(context, this.elementProvider, this.emptySlotProvider, state, offset);
    }

    @NotNull
    public Element buildPrevious(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        State state = this.states.computeIfAbsent(context,
                ctx -> new State(this.elementProvider.getEntries().size()));
        PaginatorNavigationElement element = new PaginatorNavigationElement(context, state, item);
        element.setOnClick(button -> {
            state.setPage(state.getCurrentPage() - 1);
            context.refresh();
        });
        return element;
    }

    @NotNull
    public Element buildNext(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        State state = this.states.computeIfAbsent(context,
                ctx -> new State(this.elementProvider.getEntries().size()));
        PaginatorNavigationElement element = new PaginatorNavigationElement(context, state, item);
        element.setOnClick(button -> {
            state.setPage(state.getCurrentPage() + 1);
            context.refresh();
        });
        return element;
    }

    @NotNull
    public Element buildInfo(@NotNull UIContext context, @NotNull DefinedConfigItem item) {
        State state = this.states.computeIfAbsent(context,
                ctx -> new State(this.elementProvider.getEntries().size()));
        return new PaginatorNavigationElement(context, state, item);
    }

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
