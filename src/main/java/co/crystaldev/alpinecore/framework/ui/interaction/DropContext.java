package co.crystaldev.alpinecore.framework.ui.interaction;

import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the context for an item drop event.
 *
 * @since 0.4.0
 */
public final class DropContext {

    private final ClickType type;

    private final InventoryAction action;

    private final ItemStack item;

    private ActionResult result;

    public DropContext(@NotNull ClickType type, @NotNull InventoryAction action, @NotNull ItemStack item, @NotNull ActionResult result) {
        this.type = type;
        this.action = action;
        this.item = item;
        this.result = result;
    }

    /**
     * Retrieves the click type associated with this context.
     *
     * @return the click type
     */
    @NotNull
    public ClickType type() {
        return this.type;
    }

    /**
     * Retrieves the action associated with this context.
     *
     * @return the action
     */
    @NotNull
    public InventoryAction action() {
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
    @NotNull
    public ItemStack item() {
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

    /**
     * Retrieves the result of the item drop.
     *
     * @return the {@link ActionResult} of the action
     */
    @NotNull
    public ActionResult result() {
        return this.result;
    }

    /**
     * Sets the result of the item drop.
     *
     * @param result the {@link ActionResult} to set
     */
    public void result(@NotNull ActionResult result) {
        this.result = result;
    }
}
