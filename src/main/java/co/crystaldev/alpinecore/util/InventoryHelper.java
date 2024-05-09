package co.crystaldev.alpinecore.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Utility for creating inventories.
 *
 * @since 0.4.0
 */
public final class InventoryHelper {

    private static final Method BUKKIT_CREATE_CHEST_INVENTORY = ReflectionHelper.findMethod(Bukkit.class, "createInventory",
            InventoryHolder.class, Integer.class, Component.class);
    private static final Method BUKKIT_CREATE_VARYING_INVENTORY = ReflectionHelper.findMethod(Bukkit.class, "createInventory",
            InventoryHolder.class, InventoryType.class, Component.class);

    /**
     * Creates a new inventory with the specified parameters.
     *
     * @param holder the inventory holder
     * @param slots the number of slots in the inventory
     * @param title the title of the inventory
     * @return the created inventory
     */
    @NotNull
    public static Inventory createInventory(@NotNull InventoryHolder holder, int slots, @NotNull Component title) {
        if (BUKKIT_CREATE_CHEST_INVENTORY != null) {
            return ReflectionHelper.invokeMethod(Inventory.class, null, holder, slots, title);
        }
        else {
            return Bukkit.createInventory(holder, slots, LegacyComponentSerializer.legacySection().serialize(title));
        }
    }

    /**
     * Creates a new inventory with the specified parameters.
     *
     * @param holder the inventory holder
     * @param type the type of the inventory
     * @param title the title of the inventory
     * @return the created inventory
     */
    @NotNull
    public static Inventory createInventory(@NotNull InventoryHolder holder, @NotNull InventoryType type, @NotNull Component title) {
        if (BUKKIT_CREATE_VARYING_INVENTORY != null) {
            return ReflectionHelper.invokeMethod(Inventory.class, null, holder, type, title);
        }
        else {
            return Bukkit.createInventory(holder, type, LegacyComponentSerializer.legacySection().serialize(title));
        }
    }
}
