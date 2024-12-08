package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.ElementProvider;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.element.PaginatorState;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @since 0.4.0
 */
@ApiStatus.Internal
public final class PaginatorElement<S> extends Element {

    private final ElementProvider<S, ?> elementProvider;

    private final Function<UIContext, ItemStack> emptySlotProvider;

    private final PaginatorState state;

    private final int offset;

    private Element currentElement;

    public PaginatorElement(@NotNull UIContext context,
                            @NotNull ElementProvider<S, ?> elementProvider,
                            @Nullable Function<UIContext, ItemStack> emptySlotProvider,
                            @NotNull PaginatorState state,
                            int offset) {
        super(context);
        this.elementProvider = elementProvider;
        this.emptySlotProvider = emptySlotProvider;
        this.state = state;
        this.offset = offset;

        this.setOnClick((ctx, click) -> {
            if (this.currentElement != null) {
                this.currentElement.clicked(click);
            }
        });
    }

    @Override
    public @Nullable ItemStack buildItemStack() {
        int totalOffset = this.state.getCurrentPage() * this.state.getPageSize() + this.offset;
        if (totalOffset >= this.elementProvider.size()) {
            this.currentElement = null;
            return this.emptySlotProvider == null ? null : this.emptySlotProvider.apply(this.context);
        }

        this.currentElement = this.elementProvider.getElement(this.context, totalOffset);
        this.currentElement.init();
        return this.currentElement.buildItemStack();
    }
}
