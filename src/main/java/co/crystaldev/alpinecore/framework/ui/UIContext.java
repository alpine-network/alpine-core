package co.crystaldev.alpinecore.framework.ui;

import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.type.InventoryUI;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents the state of a user interface.
 *
 * @since 0.4.0
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class UIContext {

    private final UUID playerId;

    private final InventoryUI ui;

    private Inventory inventory;

    private final UIEventBus eventBus = new UIEventBus();

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
}
