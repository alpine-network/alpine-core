package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.UIElement;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.4.0
 */
public final class GenericUIElement extends UIElement {

    private final ItemStack itemStack;

    public GenericUIElement(@NotNull UIContext context, @NotNull ItemStack itemStack) {
        super(context);
        this.itemStack = itemStack;
    }

    @Override
    public void init() {
        // NO OP
    }

    @Override
    public @Nullable ItemStack buildItemStack() {
        return this.itemStack;
    }
}
