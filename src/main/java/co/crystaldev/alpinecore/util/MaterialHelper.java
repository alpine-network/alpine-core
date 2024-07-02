package co.crystaldev.alpinecore.util;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for interoperability with XMaterials
 *
 * @author BestBearr
 * @since 0.3.1
 */
@UtilityClass
public final class MaterialHelper {

    private static final short MAX_ID = 2267;

    private static final Map<Integer, XMaterial> LEGACY_MATERIALS = new HashMap<>();

    /**
     * Get the {@link XMaterial} equivalent of the given material.
     *
     * @param type The Bukkit material.
     * @return The wrapped material.
     */
    public static @NotNull XMaterial getType(@Nullable Material type) {
        if (type == null) {
            return XMaterial.AIR;
        }
        return XMaterial.matchXMaterial(type);
    }

    /**
     * Get the {@link XMaterial} equivalent of the given material.
     *
     * @param item The item stack.
     * @return The wrapped material.
     */
    public static @NotNull XMaterial getType(@Nullable ItemStack item) {
        if (item == null) {
            return XMaterial.AIR;
        }
        return XMaterial.matchXMaterial(item);
    }

    /**
     * Get the {@link XMaterial} equivalent of the given material.
     *
     * @param location The block location.
     * @return The wrapped material.
     */
    public static @NotNull XMaterial getType(@Nullable Location location) {
        if (location == null) {
            return XMaterial.AIR;
        }
        return getType(location.getBlock());
    }

    /**
     * Get the {@link XMaterial} equivalent of the given material.
     *
     * @param block The block.
     * @return The wrapped material.
     */
    public static @NotNull XMaterial getType(@Nullable Block block) {
        if (block == null) {
            return XMaterial.AIR;
        }

        if (XMaterial.supports(13)) {
            return XMaterial.matchXMaterial(block.getType());
        }
        else {
            return getType(block.getTypeId(), block.getData());
        }
    }

    /**
     * Get the XMaterial equivalent of the given material ID and data.
     *
     * @param id   The material ID.
     * @param data The material data.
     * @return The wrapped material.
     *
     * @deprecated Minecraft dropped support for item ids after version 1.12.
     */
    @Deprecated
    public static @NotNull XMaterial getType(int id, byte data) {
        // MAX_ID is inaccessible in XMaterial
        if (id < 0 || id > MAX_ID || data < 0) {
            return XMaterial.AIR;
        }

        Material resolved = Material.getMaterial(id);
        int resolvedId = resolved == null ? id : resolved.getId();

        XMaterial resolvedMaterial = LEGACY_MATERIALS.get(resolvedId << 8 | data);
        if (resolvedMaterial == null) {
            resolvedMaterial = LEGACY_MATERIALS.get(resolvedId << 8);
            return resolvedMaterial == null ? XMaterial.AIR : resolvedMaterial;
        }
        else {
            return resolvedMaterial;
        }
    }

    public static void setType(@NotNull Block block, @NotNull XMaterial type, boolean applyPhysics) {
        if (XMaterial.supports(13)) {
            block.setType(type.parseMaterial(), applyPhysics);
        }
        else {
            block.setTypeIdAndData(type.getId(), type.getData(), applyPhysics);
        }
    }

    public static void setType(@NotNull Block block, @NotNull XMaterial type) {
        setType(block, type, true);
    }

    public static void setType(@NotNull Location location, @NotNull XMaterial type, boolean applyPhysics) {
        setType(location.getBlock(), type, applyPhysics);
    }

    public static void setType(@NotNull Location location, @NotNull XMaterial type) {
        setType(location.getBlock(), type, true);
    }

    static {
        for (XMaterial value : XMaterial.values()) {
            int id = value.getId();
            byte data = value.getData();

            if (id != -1) {
                // create a hash of the combined id and data to put in the legacy materials
                int key = id << 8 | data;
                LEGACY_MATERIALS.put(key, value);
            }
        }
    }
}
