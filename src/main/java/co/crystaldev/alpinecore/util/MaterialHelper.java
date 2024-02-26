package co.crystaldev.alpinecore.util;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for interoperability with XMaterials
 *
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 0.3.1
 */
@UtilityClass
public final class MaterialHelper {

    /**
     * Get the {@link XMaterial} equivalent of the given material.
     *
     * @param type The Bukkit material.
     * @return The wrapped material.
     */
    @NotNull
    public static XMaterial getType(@NotNull Material type) {
        return XMaterial.matchXMaterial(type);
    }

    /**
     * Get the {@link XMaterial} equivalent of the given material.
     *
     * @param item The item stack.
     * @return The wrapped material.
     */
    @NotNull
    public static XMaterial getType(@NotNull ItemStack item) {
        return XMaterial.matchXMaterial(item);
    }

    /**
     * Get the {@link XMaterial} equivalent of the given material.
     *
     * @param block The block.
     * @return The wrapped material.
     */
    @NotNull
    public static XMaterial getType(@NotNull Block block) {
        if (XMaterial.supports(12)) {
            return XMaterial.matchXMaterial(block.getTypeId(), block.getData()).orElse(XMaterial.AIR);
        }
        else {
            return XMaterial.matchXMaterial(block.getType());
        }
    }

    /**
     * Get the {@link XMaterial} equivalent of the given material.
     *
     * @param location The block location.
     * @return The wrapped material.
     */
    @NotNull
    public static XMaterial getType(@NotNull Location location) {
        return getType(location.getBlock());
    }
}
