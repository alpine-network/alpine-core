package co.crystaldev.alpinecore.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Utility for interacting with {@link ItemStack}s.
 * <p>
 * This class should NOT be used if targeting 1.16+. We
 * target 1.8 for compatibility which means these methods
 * do not consider Netherite armor.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
// TODO: how can we support modern items without breaking compatibility?
// NETHERITE, TURTLE SHELL, BOW + CROSSBOW, FISHING ROD
@UtilityClass
public final class ItemUtils {
    private static final List<Material> HELMETS = Arrays.asList(
            Material.DIAMOND_HELMET,
            Material.IRON_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.GOLD_HELMET,
            Material.LEATHER_HELMET
    );

    private static final List<Material> CHESTPLATES = Arrays.asList(
            Material.DIAMOND_CHESTPLATE,
            Material.IRON_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE,
            Material.GOLD_CHESTPLATE,
            Material.LEATHER_CHESTPLATE
    );

    private static final List<Material> LEGGINGS = Arrays.asList(
            Material.DIAMOND_LEGGINGS,
            Material.IRON_LEGGINGS,
            Material.CHAINMAIL_LEGGINGS,
            Material.GOLD_LEGGINGS,
            Material.LEATHER_LEGGINGS
    );

    private static final List<Material> BOOTS = Arrays.asList(
            Material.DIAMOND_BOOTS,
            Material.IRON_BOOTS,
            Material.CHAINMAIL_BOOTS,
            Material.GOLD_BOOTS,
            Material.LEATHER_BOOTS
    );

    private static final List<Material> SWORDS = Arrays.asList(
            Material.DIAMOND_SWORD,
            Material.IRON_SWORD,
            Material.GOLD_SWORD,
            Material.STONE_SWORD,
            Material.WOOD_SWORD
    );

    private static final List<Material> AXES = Arrays.asList(
            Material.DIAMOND_AXE,
            Material.IRON_AXE,
            Material.GOLD_AXE,
            Material.STONE_AXE,
            Material.WOOD_AXE
    );

    private static final List<Material> PICKAXES = Arrays.asList(
            Material.DIAMOND_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLD_PICKAXE,
            Material.STONE_PICKAXE,
            Material.WOOD_PICKAXE
    );

    private static final List<Material> SPADES = Arrays.asList(
            Material.DIAMOND_SPADE,
            Material.IRON_SPADE,
            Material.GOLD_SPADE,
            Material.STONE_SPADE,
            Material.WOOD_SPADE
    );

    private static final List<Material> HOES = Arrays.asList(
            Material.DIAMOND_HOE,
            Material.IRON_HOE,
            Material.GOLD_HOE,
            Material.STONE_HOE,
            Material.WOOD_HOE
    );

    /**
     * @return whether an item is an armor piece
     */
    public static boolean isArmor(@NotNull ItemStack is) {
        return isHelmet(is) || isChestplate(is) || isLeggings(is) || isBoots(is);
    }

    /**
     * @return whether an item is a helmet
     */
    public static boolean isHelmet(@NotNull ItemStack is) {
        return HELMETS.contains(is.getType());
    }

    /**
     * @return whether an item is a chestplate
     */
    public static boolean isChestplate(@NotNull ItemStack is) {
        return CHESTPLATES.contains(is.getType());
    }

    /**
     * @return whether an item is a pair of leggings
     */
    public static boolean isLeggings(@NotNull ItemStack is) {
        return LEGGINGS.contains(is.getType());
    }

    /**
     * @return whether an item is a pair of boots
     */
    public static boolean isBoots(@NotNull ItemStack is) {
        return BOOTS.contains(is.getType());
    }

    /**
     * @return whether an item is a melee weapon
     */
    public static boolean isMeleeWeapon(@NotNull ItemStack is) {
        return isSword(is) || isAxe(is);
    }

    /**
     * @return whether an item is a tool
     */
    public static boolean isTool(@NotNull ItemStack is) {
        return isAxe(is) || isPickaxe(is) || isSpade(is) || isHoe(is) || is.getType() == Material.SHEARS;
    }

    /**
     * @return whether an item is a sword
     */
    public static boolean isSword(@NotNull ItemStack is) {
        return SWORDS.contains(is.getType());
    }

    /**
     * @return whether an item is an axe
     */
    public static boolean isAxe(@NotNull ItemStack is) {
        return AXES.contains(is.getType());
    }

    /**
     * @return whether an item is a pickaxe
     */
    public static boolean isPickaxe(@NotNull ItemStack is) {
        return PICKAXES.contains(is.getType());
    }

    /**
     * @return whether an item is a spade
     */
    public static boolean isSpade(@NotNull ItemStack is) {
        return SPADES.contains(is.getType());
    }

    /**
     * @return whether an item is a hoe
     */
    public static boolean isHoe(@NotNull ItemStack is) {
        return HOES.contains(is.getType());
    }
}
