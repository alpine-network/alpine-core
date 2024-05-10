package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.ui.element.UIElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public final class EmptyUIElement extends UIElement {

    private static final EmptyUIElement EMPTY = new EmptyUIElement();

    private static final ItemStack EMPTY_ITEM_STACK = new ItemStack(Material.AIR, 1);

    private EmptyUIElement() {
        super(null);
    }

    @Override
    public void init() {
        // NO OP
    }

    @Override
    public @NotNull ItemStack buildItemStack() {
        return EMPTY_ITEM_STACK;
    }

    @NotNull
    public static EmptyUIElement empty() {
        return EMPTY;
    }
}
