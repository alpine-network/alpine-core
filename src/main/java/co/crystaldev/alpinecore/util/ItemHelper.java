package co.crystaldev.alpinecore.util;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for interacting with {@link ItemStack}s.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@UtilityClass
public final class ItemHelper {

    // region Categories

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
    public static boolean isMeleeWeapon(@NotNull ItemStack item) {
        return SWORDS.test(item) || AXES.test(item);
    }

    /**
     * @return whether an item is an armor piece
     */
    public static boolean isArmor(@NotNull ItemStack item) {
        return ARMOR.test(item);
    }

    /**
     * @return whether an item is a tool
     */
    public static boolean isTool(@NotNull ItemStack item) {
        return TOOLS.test(item);
    }

    // endregion Categories

    // region Meta

    private static final Method ITEM_META_GET_DISPLAY_NAME = ReflectionHelper.findMethod(
            ItemMeta.class, "displayName");

    private static final Method ITEM_META_SET_DISPLAY_NAME = ReflectionHelper.findMethod(
            ItemMeta.class, "displayName", Component.class);

    private static final Method ITEM_META_GET_LORE = ReflectionHelper.findMethod(
            ItemMeta.class, "lore");

    private static final Method ITEM_META_SET_LORE = ReflectionHelper.findMethod(
            ItemMeta.class, "lore", List.class);

    /**
     * Fetches the display name of an {@link ItemStack} in {@link Component} form.
     *
     * @param item The item.
     * @return the display name.
     */
    public static @NotNull Component getDisplayName(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasDisplayName()) {
            return LocaleHelper.getTranslation(item);
        }

        if (ITEM_META_GET_DISPLAY_NAME != null) {
            return ReflectionHelper.invokeMethod(ITEM_META_GET_DISPLAY_NAME, meta);
        }
        else {
            return LegacyComponentSerializer.legacySection().deserialize(meta.getDisplayName());
        }
    }

    /**
     * Sets the display name of an {@link ItemStack}.
     *
     * @param item The item.
     * @param name The name.
     */
    public static void setDisplayName(@NotNull ItemStack item, @NotNull Component name) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        if (ITEM_META_SET_DISPLAY_NAME != null) {
            ReflectionHelper.invokeMethod(ITEM_META_SET_DISPLAY_NAME, meta, name);
        }
        else {
            String serialized = LegacyComponentSerializer.legacySection().serialize(name);
            meta.setDisplayName(serialized);
        }

        item.setItemMeta(meta);
    }

    /**
     * Fetches the lore of an {@link ItemStack} in {@link Component} form.
     *
     * @param item The item.
     * @return the lore.
     */
    public static @NotNull List<Component> getLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasLore()) {
            return Collections.emptyList();
        }

        if (ITEM_META_GET_LORE != null) {
            return ReflectionHelper.invokeMethod(ITEM_META_GET_LORE, meta);
        }
        else {
            return meta.getLore()
                    .stream()
                    .map(LegacyComponentSerializer.legacySection()::deserialize)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Fetches the lore of an {@link ItemStack} in {@link Component} form.
     *
     * @param item The item.
     * @return the lore.
     */
    public static @NotNull Component getJoinedLore(@NotNull ItemStack item) {
        List<Component> lore = getLore(item);
        return lore.isEmpty() ? Component.empty() : Components.joinNewLines(lore);
    }

    /**
     * Sets the display name of an {@link ItemStack}.
     *
     * @param item The item.
     * @param lore The lore.
     */
    public static void setLore(@NotNull ItemStack item, @NotNull List<Component> lore) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        // since the lore does not allow newlines, we must split at each newline
        List<Component> processedLore = lore.isEmpty() ? lore : Components.split(Components.joinNewLines(lore), "\n");

        if (ITEM_META_SET_LORE != null) {
            ReflectionHelper.invokeMethod(ITEM_META_SET_LORE, meta, processedLore);
        }
        else {
            List<String> serialized = processedLore.stream()
                    .map(LegacyComponentSerializer.legacySection()::serialize)
                    .collect(Collectors.toList());
            meta.setLore(serialized);
        }

        item.setItemMeta(meta);
    }

    /**
     * Fetches the enchantments of an {@link ItemStack} in {@link Component} form.
     *
     * @param item The item.
     * @return the enchantments.
     */
    public static @NotNull List<Component> getEnchantment(@NotNull ItemStack item) {
        return item.getEnchantments().entrySet().stream()
                .map(entry -> {
                    XEnchantment xEnchantment = XEnchantment.matchXEnchantment(entry.getKey());
                    Component romanLevel = Component.text(RomanNumerals.convertTo(entry.getValue()));
                    return LocaleHelper.getTranslation(xEnchantment)
                            .append(Component.text(" "))
                            .append(romanLevel)
                            .color(NamedTextColor.GRAY);
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates a hover component for the given item stack.
     *
     * @param itemStack The item stack.
     * @return The hover component.
     */
    @NotNull
    public Component createHoverComponent(@NotNull ItemStack itemStack) {
        Component displayName = getDisplayName(itemStack);

        List<Component> hoverContent = new ArrayList<>();
        hoverContent.add(displayName);
        hoverContent.addAll(getEnchantment(itemStack));
        hoverContent.addAll(getLore(itemStack));

        return displayName.hoverEvent(Components.joinNewLines(hoverContent));
    }

    // endregion Meta
}
