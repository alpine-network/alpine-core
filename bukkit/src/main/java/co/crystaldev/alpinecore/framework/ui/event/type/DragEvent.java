package co.crystaldev.alpinecore.framework.ui.event.type;

import co.crystaldev.alpinecore.framework.ui.event.UIEvent;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@Getter
public final class DragEvent extends UIEvent {

    private final InventoryDragEvent handle;

    public DragEvent(@NotNull InventoryDragEvent handle) {
        super(handle.getInventorySlots());
        this.handle = handle;
    }
}
