package co.crystaldev.alpinecore.framework.ui;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.handler.UIHandler;
import co.crystaldev.alpinecore.framework.ui.type.ConfigInventoryUI;
import co.crystaldev.alpinecore.framework.ui.type.InventoryUI;
import co.crystaldev.alpinecore.util.Formatting;
import co.crystaldev.alpinecore.util.InventoryHelper;
import com.google.common.annotations.Beta;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user interface state.
 *
 * @since 0.4.0
 */
@Beta
public final class AlpineUIManager {

    @Getter
    private final AlpinePlugin plugin;

    private final Map<UUID, UIState> states = new ConcurrentHashMap<>();

    public AlpineUIManager(@NotNull AlpinePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new UIListener(this), plugin);
    }

    /**
     * Registers a player with a specified inventory user interface.
     *
     * @param player the player to register
     * @param ui     the inventory user interface
     * @param force  whether to override displayed ui
     */
    public void open(@NotNull Player player, @NotNull InventoryUI ui, boolean force) {
        UIState state = this.states.computeIfAbsent(player.getUniqueId(), k -> new UIState(player));
        UIHolder holder = new UIHolder(state);
        ConfigInventoryUI properties = ui.getProperties();

        // force close if requested
        if (force) {
            // close active contexts
            while (!state.isEmpty()) {
                this.closeContext(state.pop());
            }
        }

        // create the inventory
        Component title = this.plugin.getMiniMessage().deserialize(Formatting.placeholders(this.plugin, properties.getName()));
        Inventory inventory;
        if (ui.getType() == GuiType.CHEST) {
            inventory = InventoryHelper.createInventory(holder, properties.getSlots().length * 9, title);
        }
        else {
            inventory = InventoryHelper.createInventory(holder, ui.getType().getInventoryType(), title);
        }

        // initialize the context
        UUID id = player.getUniqueId();
        UIContext context = new UIContext(id, ui, inventory);
        holder.setContext(context);

        // push the context
        state.push(context);

        // open the inventory
        this.open(player, context, true);
    }

    /**
     * Registers a player with a specified inventory user interface.
     *
     * @param player the player to register
     * @param ui     the inventory user interface
     */
    public void open(@NotNull Player player, @NotNull InventoryUI ui) {
        this.open(player, ui, false);
    }

    /**
     * Closes the inventory of a player.
     * <p>
     * If openParent is true, it will attempt to open the parent UIContext
     * if one exists after closing the current context.
     *
     * @param player the UUID of the player
     * @param openParent a boolean indicating whether to open the parent context
     */
    public void close(@NotNull Player player, boolean openParent) {
        UUID playerId = player.getUniqueId();

        UIState state = this.states.get(playerId);

        // close the current context
        this.closeContext(state.pop());

        if (!openParent || state.isEmpty()) {
            // unregister the contexts
            this.states.remove(playerId);

            // close active contexts
            while (!state.isEmpty()) {
                this.closeContext(state.pop());
            }

            // close the inventory
            player.closeInventory();
        }
        else {
            // open the parent screen
            this.open(player, state.peek(), false);
        }
    }

    /**
     * Closes the given context.
     *
     * @param context the context to be closed
     */
    private void closeContext(@NotNull UIContext context) {
        // notify the ui handler
        context.ui().getHandler().closed(context);

        // notify elements
        for (Element element : context.getElements()) {
            element.closed();
        }

        // mark the context as stale
        context.setStale(true);
    }

    /**
     * Called when the player closes a managed inventory.
     *
     * @param inventory the inventory to be closed
     */
    void onClose(@NotNull Inventory inventory) {
        UIHolder holder = (UIHolder) inventory.getHolder();
        UIState state = holder.getState();

        Player player = state.getPlayer();
        if (!player.isOnline() || state.isEmpty()) {
            this.states.remove(player.getUniqueId());
            return;
        }

        if (holder.getContext().equals(state.peek())) {
            this.close(player, true);
        }
    }

    /**
     * Retrieves the state of a user interface for a specific player.
     *
     * @param player the UUID of the player
     * @return the UIState object representing the state of the user interface, or null if the player has no state
     */
    @Nullable
    public UIContext get(@NotNull UUID player) {
        return Optional.ofNullable(this.states.get(player)).map(UIState::peek).orElse(null);
    }

    /**
     * Retrieves the state of a user interface for a specific player.
     *
     * @param player the UUID of the player
     * @return the UIState object representing the state of the user interface, or null if the player has no state
     */
    @Nullable
    public UIContext get(@NotNull Player player) {
        return this.get(player.getUniqueId());
    }

    /**
     * Refreshes the inventory with the current elements in the UIContext.
     *
     * @param context the context containing the elements and inventory
     */
    public void refresh(@NotNull UIContext context) {
        Inventory inventory = context.inventory();
        inventory.clear();

        for (Element element : context.getElements()) {
            SlotPosition position = element.getPosition();
            if (position == null) {
                continue;
            }

            element.init();
            inventory.setItem(position.getSlot(), element.buildItemStack());
        }

        Player player = context.player();
        InventoryView openInventory = player.getOpenInventory();
        if (openInventory != null && openInventory.getTopInventory().equals(inventory)) {
            player.updateInventory();
        }
    }

    private void open(@NotNull Player player, @NotNull UIContext context, boolean initialize) {
        // initialize the gui
        UIHandler handler = context.ui().getHandler();
        if (initialize) {
            handler.registerEvents(context.eventBus());
        }
        context.setStale(false);
        handler.init(context);

        // fill the gui
        handler.fill(context);
        this.refresh(context);

        // display to the player
        Bukkit.getScheduler().runTask(context.plugin(), () -> player.openInventory(context.inventory()));
    }

    /**
     * Checks if an inventory is managed by the AlpineUIManager.
     *
     * @param inventory the inventory to check
     * @return true if the inventory is managed, false otherwise
     */
    boolean isManaged(@NotNull Inventory inventory) {
        InventoryHolder inventoryHolder = inventory.getHolder();
        if (!(inventoryHolder instanceof UIHolder)) {
            return false;
        }

        UIHolder holder = (UIHolder) inventoryHolder;
        UIState state = this.states.get(holder.getPlayer().getUniqueId());
        return state != null && holder.getContext().equals(state.peek());
    }
}
