package co.crystaldev.alpinecore.framework.ui;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.type.InventoryUI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Provides a context for UI operations.
 *
 * @since 0.4.0
 */
public final class UIContext {

    private final UUID playerId;

    private final InventoryUI ui;

    private final UIEventBus eventBus = new UIEventBus();

    @Getter
    private final List<Element> elements = new LinkedList<>();

    @Getter @Setter(AccessLevel.PACKAGE)
    private boolean stale;

    @Setter(AccessLevel.PACKAGE)
    private Inventory inventory;

    UIContext(@NotNull UUID playerId, @NotNull InventoryUI ui) {
        this.playerId = playerId;
        this.ui = ui;
    }

    /**
     * Returns the AlpineUIManager instance associated with this context.
     *
     * @return the AlpineUIManager instance
     */
    public @NotNull AlpineUIManager manager() {
        return this.ui.getManager();
    }

    /**
     * Returns the AlpinePlugin associated with this context.
     *
     * @return the AlpinePlugin instance
     */
    public @NotNull AlpinePlugin plugin() {
        return this.manager().getPlugin();
    }

    /**
     * Returns the event handler for the user interface.
     *
     * @return the event handler for the user interface
     */
    public @NotNull UIEventBus eventBus() {
        return this.eventBus;
    }

    /**
     * Retrieves the UUID of the player associated with this context.
     *
     * @return the UUID of the player
     */
    public @NotNull UUID playerId() {
        return this.playerId;
    }

    /**
     * Retrieves the Player object associated with this context.
     *
     * @return the Player object
     */
    public @NotNull Player player() {
        return Bukkit.getPlayer(this.playerId);
    }

    /**
     * Retrieves the InventoryUI object associated with this context.
     *
     * @return the InventoryUI object
     */
    public @NotNull InventoryUI ui() {
        return this.ui;
    }

    /**
     * Returns the inventory associated with this context.
     *
     * @return the inventory associated with this context
     */
    public @NotNull Inventory inventory() {
        return this.inventory;
    }

    /**
     * Retrieves the ItemStack at the specified SlotPosition in the inventory.
     *
     * @param slot The slot representing the position of the item in the inventory
     * @return the item at the specified position
     */
    public @NotNull ItemStack getItem(int slot) {
        return this.inventory.getItem(slot);
    }

    /**
     * Retrieves the ItemStack at the specified SlotPosition in the inventory.
     *
     * @param slot The slot representing the position of the item in the inventory
     * @return the item at the specified position
     */
    public @NotNull ItemStack getItem(@NotNull SlotPosition slot) {
        return this.inventory.getItem(slot.getSlot());
    }

    /**
     * Retrieves the ItemStack at the specified Element position.
     *
     * @param element the Element representing the position of the item in the inventory
     * @return the item at the specified position
     */
    public @NotNull ItemStack getItem(@NotNull Element element) {
        return this.inventory.getItem(element.getPosition().getSlot());
    }

    /**
     * Refreshes the inventory associated with this context.
     */
    public void refresh() {
        this.manager().refresh(this);
    }

    /**
     * Closes the inventory of a player.
     * <p>
     * If openParent is true, it will attempt to open the parent UIContext
     * if one exists after closing the current context.
     *
     * @param openParent a boolean indicating whether to open the parent context
     */
    public void close(boolean openParent) {
        this.manager().close(this.player(), openParent);
    }

    /**
     * Adds an element to the context.
     *
     * @param element the context to add
     */
    public void addElement(@NotNull Element element) {
        element.registerEvents(this.eventBus);
        this.elements.add(element);
    }

    /**
     * Removes an element from the context.
     *
     * @param element the context to remove
     */
    public void removeElement(@NotNull Element element) {
        this.eventBus.unregister(element);
        this.elements.remove(element);
    }

    /**
     * Removes the element from the context if its position matches the specified slot.
     *
     * @param slot the slot to filter elements by
     */
    public void removeElement(int slot) {
        this.elements.removeIf(element -> {
            SlotPosition position = element.getPosition();
            if (position != null && position.getSlot() == slot) {
                this.eventBus.unregister(element);
                return true;
            }
            return false;
        });
    }
}
