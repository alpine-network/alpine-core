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

    private final Inventory inventory;

    private final UIEventBus eventBus = new UIEventBus();

    @Getter
    private final List<Element> elements = new LinkedList<>();

    @Getter @Setter(AccessLevel.PACKAGE)
    private boolean stale;

    UIContext(@NotNull UUID playerId, @NotNull InventoryUI ui, @NotNull Inventory inventory) {
        this.playerId = playerId;
        this.ui = ui;
        this.inventory = inventory;
    }

    /**
     * Returns the AlpineUIManager instance associated with this context.
     *
     * @return the AlpineUIManager instance
     */
    @NotNull
    public AlpineUIManager manager() {
        return this.ui.getManager();
    }

    /**
     * Returns the AlpinePlugin associated with this context.
     *
     * @return the AlpinePlugin instance
     */
    @NotNull
    public AlpinePlugin plugin() {
        return this.manager().getPlugin();
    }

    /**
     * Returns the event handler for the user interface.
     *
     * @return the event handler for the user interface
     */
    @NotNull
    public UIEventBus eventBus() {
        return this.eventBus;
    }

    /**
     * Retrieves the UUID of the player associated with this context.
     *
     * @return the UUID of the player
     */
    @NotNull
    public UUID playerId() {
        return this.playerId;
    }

    /**
     * Retrieves the Player object associated with this context.
     *
     * @return the Player object
     */
    @NotNull
    public Player player() {
        return Bukkit.getPlayer(this.playerId);
    }

    /**
     * Retrieves the InventoryUI object associated with this context.
     *
     * @return the InventoryUI object
     */
    @NotNull
    public InventoryUI ui() {
        return this.ui;
    }

    /**
     * Returns the inventory associated with this context.
     *
     * @return the inventory associated with this context
     */
    @NotNull
    public Inventory inventory() {
        return this.inventory;
    }

    /**
     * Refreshes the inventory associated with this context.
     */
    public void refresh() {
        this.manager().refresh(this);
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

    /**
     * Checks if any element in the context can transfer items in the user's inventory.
     *
     * @return true if at least one element can transfer items, false otherwise
     */
    public boolean canTransferItems() {
        return elements.stream().anyMatch(Element::canTransferItems);
    }
}
