package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the context for a click event.
 *
 * @since 0.4.0
 */
public final class ClickContext {

    private final ClickType type;

    private final InventoryAction action;

    private final ItemStack item;

    private ActionResult result;

    private boolean consumedItem;

    public ClickContext(@NotNull ClickType type, @NotNull InventoryAction action, @Nullable ItemStack item, @NotNull ActionResult result) {
        this.type = type;
        this.action = action;
        this.item = item;
        this.result = result;
    }

    /**
     * Retrieves the click type associated with this click context.
     *
     * @return the click type
     */
    @NotNull
    public ClickType type() {
        return this.type;
    }

    /**
     * Retrieves the action associated with this click context.
     *
     * @return the action
     */
    @NotNull
    public InventoryAction action() {
        return this.action;
    }

    /**
     * Checks if this click context has an item.
     *
     * @return true if this click context has an item, false otherwise
     */
    public boolean hasItem() {
        return this.item != null && this.item.getType() != Material.AIR && this.item.getAmount() > 0;
    }

    /**
     * Retrieves the item associated with this click context.
     *
     * @return the item, or null if there is no item
     */
    @Nullable
    public ItemStack item() {
        return this.item;
    }

    /**
     * Consumes the item associated with this click context.
     * <p>
     * This method removes the item from the cursor.
     *
     * @return the item, or null if there is no item
     */
    @Nullable
    public ItemStack consumeItem() {
        this.consumedItem = true;
        return this.item;
    }

    /**
     * Determines if the item associated with this click context has been consumed.
     *
     * @return true if the item has been consumed, false otherwise
     */
    public boolean consumedItem() {
        return this.consumedItem;
    }

    /**
     * Retrieves the result of the click.
     *
     * @return the {@link ActionResult} of the action
     */
    @NotNull
    public ActionResult result() {
        return this.result;
    }

    /**
     * Sets the result of the click.
     *
     * @param result the {@link ActionResult} to set
     */
    public void result(@NotNull ActionResult result) {
        this.result = result;
    }
}
