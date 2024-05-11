package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a generic UI element.
 *
 * @since 0.4.0
 */
public final class GenericElement extends Element {

    private final ItemStack itemStack;

    public GenericElement(@NotNull UIContext context, @NotNull ItemStack itemStack) {
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
