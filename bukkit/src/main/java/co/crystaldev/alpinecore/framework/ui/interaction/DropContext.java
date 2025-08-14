package co.crystaldev.alpinecore.framework.ui.interaction;

import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the context for an item drop event.
 *
 * @since 0.4.0
 */
public final class DropContext extends InteractContext {

    private final ClickType type;

    private final InventoryAction action;

    private final ItemStack item;

    public DropContext(@NotNull ClickType type, @NotNull InventoryAction action, @NotNull ItemStack item, @NotNull ActionResult result) {
        super(result);
        this.type = type;
        this.action = action;
        this.item = item;
    }

    /**
     * Retrieves the click type associated with this context.
     *
     * @return the click type
     */
    public @NotNull ClickType type() {
        return this.type;
    }

    /**
     * Retrieves the action associated with this context.
     *
     * @return the action
     */
    public @NotNull InventoryAction action() {
        return this.action;
    }

    /**
     * Checks if this context has an item.
     *
     * @return true if this context has an item, false otherwise
     */
    public boolean hasItem() {
        return this.item != null && this.item.getType() != Material.AIR && this.item.getAmount() > 0;
    }

    /**
     * Retrieves the item associated with this context.
     *
     * @return the item, or null if there is no item
     */
    public @NotNull ItemStack item() {
        return this.item;
    }

    /**
     * Retrieves the amount of the item associated with this context.
     *
     * @return the amount of the item
     */
    public int amount() {
        if (this.action == InventoryAction.DROP_ONE_CURSOR) {
            return 1;
        }
        else {
            return this.item.getAmount();
        }
    }
}
