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

    @Nullable
    public T nextElement(@NotNull UIContext context) {
        State<S> state = this.states.computeIfAbsent(context, ctx -> new State<>(this.entries.iterator()));
        return state.hasNext() ? this.toElementFunction.apply(context, state.next()) : null;
    }

    @NotNull
    public T getElement(@NotNull UIContext context, int index) {
        Validate.isTrue(index >= 0 && index < this.entries.size(), "index out of bounds");
        return this.toElementFunction.apply(context, this.entries.stream().skip(index).findFirst().orElse(null));
    }

    public int getIndex(@NotNull UIContext context) {
        State<S> state = this.states.get(context);
        return state == null ? 0 : state.index;
    }

    public int size() {
        return this.entries.size();
    }

    public void closed(@NotNull UIContext context) {
        this.states.entrySet().removeIf(e -> e.getKey().isStale() || e.getKey().playerId().equals(context.playerId()));
    }

    @NotNull
    public static <S, T extends Element> Builder<S, T> builder() {
        return new Builder<>();
    }

    @RequiredArgsConstructor
    private static final class State<S> {
        private final Iterator<S> iterator;
        private int index;

        @NotNull
        public S next() {
            this.index++;
            return this.iterator.next();
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }
    }

    public static final class Builder<S, T extends Element> {
        private Collection<S> entries;
        private BiFunction<UIContext, S, T> toElementFunction;

        @NotNull
        public Builder<S, T> entries(@NotNull Collection<S> entries) {
            Validate.notNull(entries, "entries cannot be null");
            Validate.isTrue(!entries.isEmpty(), "entries cannot be empty");
            this.entries = entries;
            return this;
        }

        @NotNull
        public Builder<S, T> entries(@NotNull S... entries) {
            Validate.notNull(entries, "entries cannot be null");
            Validate.isTrue(entries.length > 0, "entries cannot be empty");
            this.entries = ImmutableList.copyOf(entries);
            return this;
        }

        @NotNull
        public Builder<S, T> element(@NotNull BiFunction<UIContext, S, T> toElementFunction) {
            Validate.notNull(toElementFunction, "toElementFunction cannot be null");
            this.toElementFunction = toElementFunction;
            return this;
        }

        @NotNull
        public ElementPaginator<S> createPaginator() {
            return ElementPaginator.from(this.build());
        }

        @NotNull
        public ElementProvider<S, T> build() {
            Validate.notNull(this.entries, "entries cannot be null");
            Validate.notNull(this.toElementFunction, "toElementFunction cannot be null");
            return new ElementProvider<>(this.entries, this.toElementFunction);
        }
    }
}
