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

    private static boolean hasType(int id, byte data) {
        XMaterial resolvedMaterial = LEGACY_MATERIALS.get(id << 8 | data);
        return resolvedMaterial != null || LEGACY_MATERIALS.get(id << 8) != null;
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

        if (!XMaterial.supports(13)) {
            // Some types such as repeaters and comparators were flattened into one type
            // resulting in them not getting registered
            for (Material value : Material.values()) {
                int id = value.getId();
                for (int data = 0; data < 16; data++) {
                    if (!hasType(id, (byte) data)) {
                        LEGACY_MATERIALS.put(id << 8 | data, XMaterial.matchXMaterial(value));
                    }
                }
            }

            // Register edge-cases
            LEGACY_MATERIALS.put(Material.BED_BLOCK.getId() << 8, XMaterial.RED_BED);
            LEGACY_MATERIALS.put(Material.FLOWER_POT.getId() << 8, XMaterial.FLOWER_POT);
            LEGACY_MATERIALS.put(Material.SKULL.getId() << 8, XMaterial.SKELETON_SKULL);

            LEGACY_MATERIALS.put(Material.SIGN_POST.getId() << 8, XMaterial.OAK_SIGN);
            LEGACY_MATERIALS.put(Material.WALL_SIGN.getId() << 8, XMaterial.OAK_WALL_SIGN);
            LEGACY_MATERIALS.put(Material.WOOD_PLATE.getId() << 8, XMaterial.OAK_PRESSURE_PLATE);
            LEGACY_MATERIALS.put(Material.TRAP_DOOR.getId() << 8, XMaterial.OAK_TRAPDOOR);

            LEGACY_MATERIALS.put(Material.DOUBLE_PLANT.getId() << 8, XMaterial.SUNFLOWER);
            LEGACY_MATERIALS.put(Material.DOUBLE_PLANT.getId() << 8 | 1, XMaterial.LILAC);
            LEGACY_MATERIALS.put(Material.DOUBLE_PLANT.getId() << 8 | 2, XMaterial.TALL_GRASS);
            LEGACY_MATERIALS.put(Material.DOUBLE_PLANT.getId() << 8 | 3, XMaterial.LARGE_FERN);
            LEGACY_MATERIALS.put(Material.DOUBLE_PLANT.getId() << 8 | 4, XMaterial.ROSE_BUSH);
            LEGACY_MATERIALS.put(Material.DOUBLE_PLANT.getId() << 8 | 5, XMaterial.PEONY);

            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8, XMaterial.SMOOTH_STONE_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 1, XMaterial.SANDSTONE_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 2, XMaterial.PETRIFIED_OAK_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 3, XMaterial.COBBLESTONE_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 4, XMaterial.BRICK_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 5, XMaterial.STONE_BRICK_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 6, XMaterial.NETHER_BRICK_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 7, XMaterial.QUARTZ_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 8, XMaterial.SMOOTH_STONE);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 9, XMaterial.SMOOTH_SANDSTONE);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 10, XMaterial.PETRIFIED_OAK_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 11, XMaterial.COBBLESTONE_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 12, XMaterial.BRICK_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 13, XMaterial.STONE_BRICK_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 14, XMaterial.NETHER_BRICK_SLAB);
            LEGACY_MATERIALS.put(Material.DOUBLE_STEP.getId() << 8 | 15, XMaterial.SMOOTH_QUARTZ);
        }
    }
}
