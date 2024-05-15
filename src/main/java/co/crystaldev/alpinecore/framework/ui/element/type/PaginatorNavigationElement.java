package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.element.ElementPaginator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@ApiStatus.Internal
public final class PaginatorNavigationElement extends Element {

    private final ElementPaginator.State state;

    private final DefinedConfigItem item;

    public PaginatorNavigationElement(@NotNull UIContext context, @NotNull ElementPaginator.State state,
                                      @NotNull DefinedConfigItem item) {
        super(context);
        this.state = state;
        this.item = item;
    }

    @Override
    public @NotNull ItemStack buildItemStack() {
        int page = this.state.getCurrentPage() + 1;
        return this.item.build(this.context.manager().getPlugin(),
                "page", page,
                "previous_page", Math.max(1, page - 1),
                "next_page", Math.min(this.state.getMaxPages(), page + 1),
                "page_count", this.state.getMaxPages(),
                "page_size", this.state.getPageSize(),
                "element_count", this.state.getElementCount()
        );
    }
}
