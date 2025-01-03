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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
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
public final class UIManager {

    @Getter
    private final AlpinePlugin plugin;

    private final Map<UUID, UIState> states = new ConcurrentHashMap<>();

    public UIManager(@NotNull AlpinePlugin plugin) {
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

        // initialize the context
        UUID id = player.getUniqueId();
        UIContext context = new UIContext(id, ui);
        holder.setContext(context);

        // create the inventory
        Component title = this.plugin.getMiniMessage().deserialize(Formatting.placeholders(this.plugin,
                properties.getName(), ui.getHandler().getTitlePlaceholders(context)));
        if (ui.getType() == GuiType.CHEST) {
            context.setInventory(InventoryHelper.createInventory(holder, properties.getSlots().length * 9, title));
        }
        else {
            context.setInventory(InventoryHelper.createInventory(holder, ui.getType().getInventoryType(), title));
        }

        // push the context
        UIContext lastContext = state.push(context);
        if (lastContext != null) {
            this.closeContext(lastContext);
        }

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
     * Swaps the user interface for a player with a new one.
     *
     * @param player the player whose user interface is being swapped
     * @param ui     the new user interface to swap with
     */
    public void swap(@NotNull Player player, @NotNull InventoryUI ui) {
        UIState state = this.states.computeIfAbsent(player.getUniqueId(), k -> new UIState(player));

        if (!state.isEmpty()) {
            this.closeContext(state.pop());
        }

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

        // empty the inventory
        if (this.plugin.isEnabled()) {
            Inventory inventory = context.inventory();
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                inventory.clear();

                Player player = context.player();
                if (player != null && player.isOnline()) {
                    player.updateInventory();
                }
            }, 1L);
        }
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

        UIContext context = holder.getContext();
        if (context.equals(state.peek())) {
            UIHandler handler = context.ui().getHandler();
            this.close(player, handler.openParentOnClose(context));
        }
    }

    /**
     * Called every server tick to update open UIs.
     *
     * @param tick the current tick.
     */
    void onTick(long tick) {
        if (this.states.isEmpty()) {
            return;
        }

        this.states.forEach((playerId, state) -> {
            Player player = Bukkit.getPlayer(playerId);

            if (player != null && !state.isEmpty()) {
                UIContext context = state.peek();
                UIHandler handler = context.ui().getHandler();
                handler.tick(context);
            }
        });
    }

    /**
     * Retrieves the state of a user interface for a specific player.
     *
     * @param player the UUID of the player
     * @return the UIState object representing the state of the user interface, or null if the player has no state
     */
    public @Nullable UIContext get(@NotNull UUID player) {
        return Optional.ofNullable(this.states.get(player)).map(UIState::peek).orElse(null);
    }

    /**
     * Retrieves the state of a user interface for a specific player.
     *
     * @param player the UUID of the player
     * @return the UIState object representing the state of the user interface, or null if the player has no state
     */
    public @Nullable UIContext get(@NotNull Player player) {
        return this.get(player.getUniqueId());
    }

    /**
     * Refreshes the inventory with the current elements in the UIContext.
     *
     * @param context the context containing the elements and inventory
     */
    public void refresh(@NotNull UIContext context) {

        // notify the handler
        UIHandler handler = context.ui().getHandler();
        handler.beforeRefresh(context);

        // clear the inventory
        Inventory inventory = context.inventory();
        inventory.clear();

        // redraw all elements
        for (Element element : context.getElements()) {
            SlotPosition position = element.getPosition();
            if (position == null) {
                continue;
            }

            element.init();
            ItemStack item = element.buildItemStack();
            if (item != null || element.allowsEmptyItems()) {
                inventory.setItem(position.getSlot(), item);
            }
        }

        // notify the handler
        handler.afterRefresh(context);

        // update the gui
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

    @ApiStatus.Internal
    public void closeAll() {
        this.states.keySet().forEach(playerId -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                this.close(player, false);
            }
        });
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
