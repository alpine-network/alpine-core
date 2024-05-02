package co.crystaldev.alpinecore.util;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for interacting with {@link ItemStack}s.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@UtilityClass
public final class ItemHelper {

    public static final MappedMaterial HELMETS = MappedMaterial.of(
            XMaterial.NETHERITE_HELMET,
            XMaterial.DIAMOND_HELMET,
            XMaterial.IRON_HELMET,
            XMaterial.CHAINMAIL_HELMET,
            XMaterial.GOLDEN_HELMET,
            XMaterial.LEATHER_HELMET,
            XMaterial.TURTLE_HELMET
    );

    public static final MappedMaterial CHESTPLATES = MappedMaterial.of(
            XMaterial.NETHERITE_CHESTPLATE,
            XMaterial.DIAMOND_CHESTPLATE,
            XMaterial.IRON_CHESTPLATE,
            XMaterial.CHAINMAIL_CHESTPLATE,
            XMaterial.GOLDEN_CHESTPLATE,
            XMaterial.LEATHER_CHESTPLATE
    );

    public static final MappedMaterial LEGGINGS = MappedMaterial.of(
            XMaterial.NETHERITE_LEGGINGS,
            XMaterial.DIAMOND_LEGGINGS,
            XMaterial.IRON_LEGGINGS,
            XMaterial.CHAINMAIL_LEGGINGS,
            XMaterial.GOLDEN_LEGGINGS,
            XMaterial.LEATHER_LEGGINGS
    );

    public static final MappedMaterial BOOTS = MappedMaterial.of(
            XMaterial.NETHERITE_BOOTS,
            XMaterial.DIAMOND_BOOTS,
            XMaterial.IRON_BOOTS,
            XMaterial.CHAINMAIL_BOOTS,
            XMaterial.GOLDEN_BOOTS,
            XMaterial.LEATHER_BOOTS
    );

    public static final MappedMaterial SWORDS = MappedMaterial.of(
            XMaterial.NETHERITE_SWORD,
            XMaterial.DIAMOND_SWORD,
            XMaterial.IRON_SWORD,
            XMaterial.GOLDEN_SWORD,
            XMaterial.STONE_SWORD,
            XMaterial.WOODEN_SWORD
    );

    public static final MappedMaterial AXES = MappedMaterial.of(
            XMaterial.NETHERITE_AXE,
            XMaterial.DIAMOND_AXE,
            XMaterial.IRON_AXE,
            XMaterial.GOLDEN_AXE,
            XMaterial.STONE_AXE,
            XMaterial.WOODEN_AXE
    );

    public static final MappedMaterial PICKAXES = MappedMaterial.of(
            XMaterial.NETHERITE_PICKAXE,
            XMaterial.DIAMOND_PICKAXE,
            XMaterial.IRON_PICKAXE,
            XMaterial.GOLDEN_PICKAXE,
            XMaterial.STONE_PICKAXE,
            XMaterial.WOODEN_PICKAXE
    );

    public static final MappedMaterial SHOVELS = MappedMaterial.of(
            XMaterial.NETHERITE_SHOVEL,
            XMaterial.DIAMOND_SHOVEL,
            XMaterial.IRON_SHOVEL,
            XMaterial.GOLDEN_SHOVEL,
            XMaterial.STONE_SHOVEL,
            XMaterial.WOODEN_SHOVEL
    );

    public static final MappedMaterial HOES = MappedMaterial.of(
            XMaterial.NETHERITE_HOE,
            XMaterial.DIAMOND_HOE,
            XMaterial.IRON_HOE,
            XMaterial.GOLDEN_HOE,
            XMaterial.STONE_HOE,
            XMaterial.WOODEN_HOE
    );

    public static final MappedMaterial ARMOR = MappedMaterial.builder()
            .add(HELMETS).add(CHESTPLATES).add(LEGGINGS).add(BOOTS)
            .build();

    public static final MappedMaterial TOOLS = MappedMaterial.builder()
            .add(AXES).add(PICKAXES).add(SHOVELS).add(HOES)
            .build();

    /**
     * @return whether an item is a melee weapon
     */
    public static boolean isMeleeWeapon(@NotNull ItemStack is) {
        return SWORDS.test(is) || AXES.test(is);
    }

    /**
     * @return whether an item is an armor piece
     */
    public static boolean isArmor(@NotNull ItemStack is) {
        return ARMOR.test(is);
    }

    /**
     * @return whether an item is a tool
     */
    public static boolean isTool(@NotNull ItemStack is) {
        return TOOLS.test(is);
    }
}
