package co.crystaldev.alpinecore.util;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import me.pikamug.localelib.LocaleManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility for interacting with LocaleLib.
 *
 * @see <a href=https://www.spigotmc.org/resources/localelib.65617/>LocaleLib Spigot Page</a>
 * @since 0.4.0
 */
@UtilityClass
public final class LocaleHelper {

    private static final LocaleManager MANAGER = new LocaleManager();

    /**
     * Retrieves the translation key for a given material.
     *
     * @param material the material
     * @return the translation key
     */
    public static @NotNull String getTranslationKey(@NotNull XMaterial material) {
        try {
            return MANAGER.queryMaterial(material.parseMaterial());
        }
        catch (Exception ex) {
            return formatEnum(material.parseMaterial());
        }
    }

    /**
     * Retrieves the translation of a given material as a component.
     *
     * @param material the material
     * @return the translated component
     */
    public static @NotNull Component getTranslation(@NotNull XMaterial material) {
        try {
            return Component.translatable(MANAGER.queryMaterial(material.parseMaterial()));
        }
        catch (Exception ex) {
            return Component.text(formatEnum(material.parseMaterial()));
        }
    }

    /**
     * Retrieves the translation key for a given material.
     *
     * @param material the material
     * @return the translation key
     */
    public static @NotNull String getTranslationKey(@NotNull Material material) {
        try {
            return MANAGER.queryMaterial(material);
        }
        catch (Exception ex) {
            return formatEnum(material);
        }
    }

    /**
     * Retrieves the translation of a given material as a component.
     *
     * @param material the material
     * @return the translated component
     */
    public static @NotNull Component getTranslation(@NotNull Material material) {
        try {
            return Component.translatable(MANAGER.queryMaterial(material));
        }
        catch (Exception ex) {
            return Component.text(formatEnum(material));
        }
    }

    /**
     * Retrieves the translation key for a given item stack.
     *
     * @param itemStack the item stack
     * @return the translation key
     */
    public static @NotNull String getTranslationKey(@NotNull ItemStack itemStack) {
        try {
            return MANAGER.queryMaterial(itemStack.getType(), itemStack.getDurability(), itemStack.getItemMeta());
        }
        catch (Exception ex) {
            return getTranslationKey(XMaterial.matchXMaterial(itemStack));
        }
    }

    /**
     * Retrieves the translated component for a given item stack.
     *
     * @param itemStack the item stack
     * @return the translated component
     */
    public static @NotNull Component getTranslation(@NotNull ItemStack itemStack) {
        try {
            return Component.translatable(MANAGER.queryMaterial(itemStack.getType(), itemStack.getDurability(), itemStack.getItemMeta()));
        }
        catch (Exception ex) {
            return Component.text(getTranslationKey(XMaterial.matchXMaterial(itemStack)));
        }
    }

    /**
     * Retrieves the translation key for a given entity.
     *
     * @param entity the entity
     * @return the translation key
     */
    public static @NotNull String getTranslationKey(@NotNull Entity entity) {
        try {
            return MANAGER.queryEntity(entity);
        }
        catch (Exception ex) {
            return formatEnum(entity.getType());
        }
    }

    /**
     * Retrieves the translated component for a given entity.
     *
     * @param entity the entity
     * @return the translated component
     */
    public static @NotNull Component getTranslation(@NotNull Entity entity) {
        try {
            return Component.translatable(MANAGER.queryEntity(entity));
        }
        catch (Exception ex) {
            return Component.text(formatEnum(entity.getType()));
        }
    }

    /**
     * Retrieves the translation key for a given entity.
     *
     * @param entity the entity
     * @return the translation key
     */
    public static @NotNull String getTranslationKey(@NotNull EntityType entity) {
        try {
            return MANAGER.queryEntityType(entity, null);
        }
        catch (Exception ex) {
            return formatEnum(entity);
        }
    }

    /**
     * Retrieves the translation for a specific EntityType.
     *
     * @param entity the entity type
     * @return the translated component
     * @throws NullPointerException if the entity is null
     */
    public static @NotNull Component getTranslation(@NotNull EntityType entity) {
        try {
            return Component.translatable(MANAGER.queryEntityType(entity, null));
        }
        catch (Exception ex) {
            return Component.text(formatEnum(entity));
        }
    }

    private static @NotNull String formatEnum(@NotNull Enum<?> value) {
        return Stream.of(value.name().toLowerCase().split("_"))
                .map(v -> v.isEmpty() ? v : Character.toUpperCase(v.charAt(0)) + (v.length() > 1  ? v.substring(1) : ""))
                .collect(Collectors.joining(" "));
    }
}
