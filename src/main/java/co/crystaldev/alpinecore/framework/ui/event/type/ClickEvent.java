package co.crystaldev.alpinecore.framework.ui.event.type;

import co.crystaldev.alpinecore.framework.ui.event.UIEvent;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@Getter
public final class ClickEvent extends UIEvent {

    private final InventoryClickEvent handle;

    public ClickEvent(@NotNull InventoryClickEvent handle) {
        super();
        this.handle = handle;
    }
}
