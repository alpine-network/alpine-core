package co.crystaldev.alpinecore.framework.ui.type;

import co.crystaldev.alpinecore.framework.ui.GuiType;
import co.crystaldev.alpinecore.framework.ui.handler.UIHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public final class InventoryUI {

    private final ConfigInventoryUI properties;

    private final UIHandler handler;

    public InventoryUI(@NotNull ConfigInventoryUI properties, @NotNull UIHandler handler) {
        this.properties = properties;
        this.handler = handler;

        GuiType resolved = GuiType.resolveType(properties.getSlots());
    }

    public void view(@NotNull Player player) {

    }
}
