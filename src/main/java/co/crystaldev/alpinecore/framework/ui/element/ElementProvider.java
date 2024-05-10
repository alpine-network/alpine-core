package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The ElementProvider class provides a way to iterate over a collection of objects and
 * convert them into elements using a provided function.
 *
 * @since 0.4.0
 */
public final class ElementProvider<S, T extends UIElement> {

    private final Collection<S> entries;

    private final BiFunction<UIContext, S, T> toElementFunction;

    private final Map<UIContext, Iterator<S>> states = new HashMap<>();

    private ElementProvider(@NotNull Collection<S> entries, @NotNull BiFunction<UIContext, S, T> toElementFunction) {
        this.entries = entries;
        this.toElementFunction = toElementFunction;
    }

    public void init(@NotNull UIContext context) {
        this.states.entrySet().removeIf(e -> e.getKey().isStale() || e.getKey().playerId().equals(context.playerId()));
    }

    @Nullable
    public T nextElement(@NotNull UIContext context) {
        Iterator<S> iterator = this.states.computeIfAbsent(context, ctx -> this.entries.iterator());
        return iterator.hasNext() ? this.toElementFunction.apply(context, iterator.next()) : null;
    }

    public int size() {
        return this.entries.size();
    }

    @NotNull
    public static <S, T extends UIElement> Builder<S, T> builder() {
        return new Builder<>();
    }

    public static final class Builder<S, T extends UIElement> {
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
        public Builder<S, T> element(@NotNull Function<S, T> toElementFunction) {
            Validate.notNull(toElementFunction, "toElementFunction cannot be null");
            this.toElementFunction = (context, element) -> toElementFunction.apply(element);
            return this;
        }

        @NotNull
        public ElementProvider<S, T> build() {
            Validate.notNull(this.entries, "entries cannot be null");
            Validate.notNull(this.toElementFunction, "toElementFunction cannot be null");
            return new ElementProvider<>(this.entries, this.toElementFunction);
        }
    }
}
