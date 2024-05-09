package co.crystaldev.alpinecore.framework.ui;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.ui.handler.UIHandler;
import co.crystaldev.alpinecore.framework.ui.type.ConfigInventoryUI;
import co.crystaldev.alpinecore.framework.ui.type.InventoryUI;
import co.crystaldev.alpinecore.util.InventoryHelper;
import com.google.common.annotations.Beta;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user interface state.
 *
 * @since 0.4.0
 */
@Beta
public final class AlpineUIManager {

    private final AlpinePlugin plugin;

    private final Map<UUID, UIContext> states = new ConcurrentHashMap<>();

    public AlpineUIManager(@NotNull AlpinePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new UIListener(this), plugin);
    }

    /**
     * Registers a player with a specified inventory user interface.
     *
     * @param player the player to register
     * @param ui     the inventory user interface
     */
    public void open(@NotNull Player player, @NotNull InventoryUI ui) {
        ConfigInventoryUI properties = ui.getProperties();

        // create the inventory
        Component title = this.plugin.getMiniMessage().deserialize(properties.getName());
        Inventory inventory;
        if (ui.getType() == GuiType.CHEST) {
            inventory = InventoryHelper.createInventory(player, properties.getSlots().length * 9, title);
        }
        else {
            inventory = InventoryHelper.createInventory(player, ui.getType().getInventoryType(), title);
        }

        // initialize the context
        UUID id = player.getUniqueId();
        UIContext context = new UIContext(id, ui, inventory);
        this.states.put(id, context);

        // initialize the gui
        UIHandler handler = ui.getHandler();
        handler.registerEvents(context, context.eventBus());
        handler.init(context);

        // display to the player
        player.openInventory(inventory);
    }

    /**
     * Closes the inventory of a player.
     *
     * @param player        the UUID of the player
     * @param requiresClose determines whether the inventory should be closed
     */
    public void close(@NotNull UUID player, boolean requiresClose) {
        Player resolved = Bukkit.getPlayer(player);
        if (resolved == null || !resolved.isOnline()) {
            return;
        }

        if (requiresClose) {
            resolved.closeInventory();
        }

        this.states.remove(player);
    }

    /**
     * Closes the inventory of a player.
     *
     * @param player the UUID of the player
     */
    public void close(@NotNull UUID player) {
        this.close(player, true);
    }

    /**
     * Retrieves the state of a user interface for a specific player.
     *
     * @param player the UUID of the player
     * @return the UIState object representing the state of the user interface, or null if the player has no state
     */
    @Nullable
    public UIContext getState(@NotNull UUID player) {
        return this.states.get(player);
    }

    /**
     * Checks if a player is managed by the AlpineUIManager.
     *
     * @param player the UUID of the player
     * @return true if the player is managed, false otherwise
     */
    public boolean isManaged(@NotNull UUID player) {
        Player resolved = Bukkit.getPlayer(player);
        return resolved != null && this.states.containsKey(player);
    }
}
