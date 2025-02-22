package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.element.PaginatorState;
import co.crystaldev.alpinecore.framework.ui.interaction.ClickContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.4.0
 */
@ApiStatus.Internal
public final class PaginatorNavElement extends Element {

    private final PaginatorState state;

    private final DefinedConfigItem item, emptyItem;

    private final int direction;

    public PaginatorNavElement(@NotNull UIContext context, @NotNull PaginatorState state, int direction,
                               @NotNull DefinedConfigItem item, @Nullable DefinedConfigItem emptyItem) {
        super(context);
        this.state = state;
        this.direction = direction;
        this.item = item;
        this.emptyItem = emptyItem == null ? item : emptyItem;
    }

    @Override
    public @NotNull ItemStack buildItemStack() {
        int currentPage = this.state.getCurrentPage();
        int page = currentPage + 1;
        boolean hasPage = this.state.getMaxPages() > 1 && this.state.isValid(currentPage + this.direction);
        DefinedConfigItem configItem = hasPage ? this.item : emptyItem;

        return configItem.build(this.context.manager().getPlugin(),
                "page", page,
                "previous_page", Math.max(1, page - 1),
                "next_page", Math.min(this.state.getMaxPages(), page + 1),
                "page_count", this.state.getMaxPages(),
                "page_size", this.state.getPageSize(),
                "element_count", this.state.getElementCount()
        );
    }

    @Override
    public void clicked(@NotNull ClickContext context) {
        super.clicked(context);
        if (this.direction != 0) {
            int page = this.state.getCurrentPage() + this.direction;
            if (this.state.isValid(page)) {
                this.state.setPage(page);
                this.context.refresh();
            }
        }
    }
}
