package co.crystaldev.alpinecore.framework.ui;

import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a position in an inventory slot.
 *
 * @since 0.4.0
 */
@Data
public final class SlotPosition {

    private final Inventory inventory;

    private final int slot;

    /**
     * Retrieves the type of the inventory.
     *
     * @return The type of the GUI.
     */
    @NotNull
    public GuiType getType() {
        return GuiType.fromType(this.inventory.getType());
    }

    /**
     * Retrieves the x-coordinate of the position in an inventory slot.
     *
     * @return The x-coordinate.
     */
    public int getX() {
        return this.slot % this.getType().getRowLength();
    }

    /**
     * Retrieves the y-coordinate of the position in an inventory slot.
     *
     * @return The y-coordinate.
     */
    public int getY() {
        return this.slot / this.getType().getRowLength();
    }

    @NotNull
    public static SlotPosition from(@NotNull Inventory inventory, int slot) {
        return new SlotPosition(inventory, slot);
    }

    @NotNull
    public static SlotPosition from(@NotNull Inventory inventory, int x, int y) {
        GuiType type = GuiType.fromType(inventory.getType());
        return new SlotPosition(inventory, y * type.getRowLength() + x);
    }
}
