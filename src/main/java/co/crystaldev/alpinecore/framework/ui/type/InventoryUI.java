package co.crystaldev.alpinecore.framework.ui.type;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.ui.GuiType;
import co.crystaldev.alpinecore.framework.ui.handler.UIHandler;
import co.crystaldev.alpinecore.framework.ui.AlpineUIManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@Getter
public final class InventoryUI {

    private final ConfigInventoryUI properties;

    private final AlpineUIManager manager;

    private final UIHandler handler;

    private final GuiType type;

    public InventoryUI(@NotNull ConfigInventoryUI properties, @NotNull AlpinePlugin plugin, @NotNull UIHandler handler) {
        this.properties = properties;
        this.manager = plugin.getUiManager();
        this.handler = handler;
        this.type = GuiType.resolveType(properties.getSlots());
    }

    /**
     * Opens the inventory user interface for the specified player.
     *
     * @param player the player to open the inventory for
     */
    public void view(@NotNull Player player) {
        this.manager.open(player, this);
    }
}
