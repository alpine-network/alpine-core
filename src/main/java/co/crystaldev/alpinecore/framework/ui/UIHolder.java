package co.crystaldev.alpinecore.framework.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@RequiredArgsConstructor @Getter @Setter
public final class UIHolder implements InventoryHolder {

    private final UIState state;

    private UIContext context;

    public @NotNull Player getPlayer() {
        return this.context.player();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.context.inventory();
    }
}
