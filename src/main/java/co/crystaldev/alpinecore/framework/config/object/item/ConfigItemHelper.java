package co.crystaldev.alpinecore.framework.config.object.item;

import co.crystaldev.alpinecore.util.ReflectionHelper;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.4.8
 */
final class ConfigItemHelper {

    private static final Map<String, XEnchantment> ENCHANTMENTS = new HashMap<>();

    private static final Map<String, XPotion> POTIONS = new HashMap<>();

    private static final Method PotionMeta$setBasePotionType = ReflectionHelper.findMethod(PotionMeta.class,
            "setBasePotionType", PotionType.class);

    private static final Class<?> PotionData = ReflectionHelper.getClass(Bukkit.class.getClassLoader(),
            "org.bukkit.potion.PotionData");

    private static final Constructor<?> PotionData$init = PotionData == null ? null
            : ReflectionHelper.findConstructor(PotionData, PotionType.class);

    private static final Method PotionMeta$setBasePotionData = PotionData == null ? null
            : ReflectionHelper.findMethod(PotionMeta.class, "setBasePotionData", PotionData);

    private static final Method PotionMeta$setColor = ReflectionHelper.findMethod(PotionMeta.class,
            "setColor", Color.class);

    public static void applyToItem(@NotNull ItemStack itemStack, @NotNull Map<String, Object> attributes) {

        // Add enchantments from attributes
        ENCHANTMENTS.forEach((key, enchantment) -> {
            Object attrib = attributes.get(key);
            if (attrib == null) {
                return;
            }

            itemStack.addUnsafeEnchantment(enchantment.getEnchant(), Integer.parseInt(attrib.toString()));
        });

        ItemMeta itemMeta = itemStack.getItemMeta();
        boolean updated = false;

        // Add ItemFlags from attributes
        for (ItemFlag value : ItemFlag.values()) {
            if (attributes.containsKey(value.name())) {
                itemMeta.addItemFlags(value);
                updated = true;
            }
        }

        // Add custom potion effects from attributes
        if (itemMeta instanceof PotionMeta) {

            // Iterate over effects
            POTIONS.forEach((key, potion) -> {
                Object attrib = attributes.get(key);
                if (attrib != null) {
                    int duration, amplifier = 0;
                    if (attrib instanceof Number) {
                        duration = ((Number) attrib).intValue();
                    }
                    else if (attrib instanceof Map) {
                        Map<String, Integer> effect = (Map<String, Integer>) attrib;
                        duration = effect.getOrDefault("duration", 0);
                        amplifier = effect.getOrDefault("amplifier", 0);
                    }
                    else {
                        String[] split = attrib.toString().split(" ");
                        duration = Integer.parseInt(split[0]);
                        if (split.length > 1) {
                            amplifier = Integer.parseInt(split[1]);
                        }
                    }

                    PotionMeta potionMeta = (PotionMeta) itemMeta;
                    potionMeta.addCustomEffect(potion.buildPotionEffect(duration * 20, amplifier), true);
                    setPrimaryType(potionMeta, potion);
                }
            });

            // Set the potion color
            Object potionColor = attributes.get("potion_color");
            if (potionColor != null && PotionMeta$setColor != null) {
                int color = 0;
                if (potionColor instanceof Number) {
                    color = ((Number) potionColor).intValue();
                }
                else if (potionColor instanceof String) {
                    color = Integer.parseInt(potionColor.toString(), 16);
                }
                ReflectionHelper.invokeMethod(PotionMeta$setColor, itemMeta, Color.fromRGB(color));
            }

            // Set the primary effect
            Object primaryEffect = attributes.get("primary_effect");
            if (primaryEffect != null) {
                XPotion.matchXPotion((String) primaryEffect).ifPresent(potion -> {
                    if (!potion.isSupported()) {
                        return;
                    }

                    setPrimaryType((PotionMeta) itemMeta, potion);
                });
            }

            // Hide effects on item
            if ("true".equals(attributes.getOrDefault("hide_effects", "false").toString())) {
                itemMeta.addItemFlags(XItemFlag.HIDE_ADDITIONAL_TOOLTIP.get());
            }

            updated = true;
        }

        if (updated) {
            // Update the item meta
            itemStack.setItemMeta(itemMeta);
        }
    }

    private static void setPrimaryType(@NotNull PotionMeta meta, @NotNull XPotion type) {
        if (PotionMeta$setBasePotionType != null) {
            ReflectionHelper.invokeMethod(PotionMeta$setBasePotionType, meta, type.getPotionType());
        }
        else if (PotionMeta$setBasePotionData != null && PotionData$init != null) {
            Object potionData = ReflectionHelper.invokeConstructor(PotionData$init, type.getPotionType());
            ReflectionHelper.invokeMethod(PotionMeta$setBasePotionData, meta, potionData);
        }
        else {
            meta.setMainEffect(type.getPotionEffectType());
        }
    }

    static {
        for (XEnchantment enchantment : XEnchantment.values()) {
            if (enchantment.isSupported()) {
                ENCHANTMENTS.put("enchant_" + enchantment.name().toLowerCase(), enchantment);
            }
        }
        for (XPotion potion : XPotion.values()) {
            if (potion.isSupported()) {
                POTIONS.put("potion_" + potion.name().toLowerCase(), potion);
            }
        }
    }
}
