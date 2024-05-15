package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.ui.element.Element;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public final class EmptyElement extends Element {

    private static final EmptyElement EMPTY = new EmptyElement();

    private static final ItemStack EMPTY_ITEM_STACK = new ItemStack(Material.AIR, 1);

    private EmptyElement() {
        super(null);
    }

    @Override
    public @NotNull ItemStack buildItemStack() {
        return EMPTY_ITEM_STACK;
    }

    @NotNull
    public static EmptyElement empty() {
        return EMPTY;
    }
}
