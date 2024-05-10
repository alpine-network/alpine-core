package co.crystaldev.alpinecore.framework.ui;

import co.crystaldev.alpinecore.framework.ui.element.UIElement;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.type.InventoryUI;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

    private Inventory inventory;

    private final UIEventBus eventBus = new UIEventBus();

    @Getter
    private final List<UIElement> elements = new LinkedList<>();

    @Getter @Setter(AccessLevel.PACKAGE)
    private boolean stale;

    UIContext(@NotNull UUID playerId, @NotNull InventoryUI ui, @NotNull Inventory inventory) {
        this.playerId = playerId;
        this.ui = ui;
        this.inventory = inventory;
    }

    /**
     * Returns the AlpineUIManager instance associated with this UIContext.
     *
     * @return the AlpineUIManager instance
     */
    @NotNull
    public AlpineUIManager manager() {
        return this.ui.getManager();
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
     * Retrieves the UUID of the player associated with the UIState.
     *
     * @return the UUID of the player
     */
    @NotNull
    public UUID playerId() {
        return this.playerId;
    }

    /**
     * Retrieves the Player object associated with the UIState.
     *
     * @return the Player object
     */
    @NotNull
    public Player player() {
        return Bukkit.getPlayer(this.playerId);
    }

    /**
     * Retrieves the InventoryUI object associated with the UIState.
     *
     * @return the InventoryUI object
     */
    @NotNull
    public InventoryUI ui() {
        return this.ui;
    }

    /**
     * Returns the inventory of the UI state.
     *
     * @return the inventory of the UI state
     */
    @NotNull
    public Inventory inventory() {
        return this.inventory;
    }

    /**
     * Adds an element to the context.
     *
     * @param element the context to add
     */
    public void addElement(@NotNull UIElement element) {
        element.registerEvents(this, this.eventBus);
        this.elements.add(element);
    }

    /**
     * Removes an element from the context.
     *
     * @param element the context to remove
     */
    public void removeElement(@NotNull UIElement element) {
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
            return position != null && position.getSlot() == slot;
        });
    }
}
