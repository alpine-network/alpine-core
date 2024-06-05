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
    @NotNull
    public static String getTranslationKey(@NotNull XMaterial material) {
        return MANAGER.queryMaterial(material.parseMaterial());
    }

    /**
     * Retrieves the translation of a given material as a component.
     *
     * @param material the material
     * @return the translated component
     */
    @NotNull
    public static Component getTranslation(@NotNull XMaterial material) {
        return Component.translatable(getTranslationKey(material));
    }

    /**
     * Retrieves the translation key for a given material.
     *
     * @param material the material
     * @return the translation key
     */
    @NotNull
    public static String getTranslationKey(@NotNull Material material) {
        return MANAGER.queryMaterial(material);
    }

    /**
     * Retrieves the translation of a given material as a component.
     *
     * @param material the material
     * @return the translated component
     */
    @NotNull
    public static Component getTranslation(@NotNull Material material) {
        return Component.translatable(getTranslationKey(material));
    }

    /**
     * Retrieves the translation key for a given item stack.
     *
     * @param itemStack the item stack
     * @return the translation key
     */
    @NotNull
    public static String getTranslationKey(@NotNull ItemStack itemStack) {
        return MANAGER.queryMaterial(itemStack.getType(), itemStack.getDurability(), itemStack.getItemMeta());
    }

    /**
     * Retrieves the translated component for a given item stack.
     *
     * @param item the item stack
     * @return the translated component
     */
    @NotNull
    public static Component getTranslation(@NotNull ItemStack item) {
        return Component.translatable(getTranslationKey(item));
    }

    /**
     * Retrieves the translation key for a given entity.
     *
     * @param entity the entity
     * @return the translation key
     */
    @NotNull
    public static String getTranslationKey(@NotNull Entity entity) {
        return MANAGER.queryEntity(entity);
    }

    /**
     * Retrieves the translated component for a given entity.
     *
     * @param entity the entity
     * @return the translated component
     */
    @NotNull
    public static Component getTranslation(@NotNull Entity entity) {
        return Component.translatable(getTranslationKey(entity));
    }

    /**
     * Retrieves the translation key for a given entity.
     *
     * @param entity the entity
     * @return the translation key
     */
    @NotNull
    public static String getTranslationKey(@NotNull EntityType entity) {
        return MANAGER.queryEntityType(entity, null);
    }

    /**
     * Retrieves the translation for a specific EntityType.
     *
     * @param entity the entity type
     * @return the translated component
     * @throws NullPointerException if the entity is null
     */
    @NotNull
    public static Component getTranslation(@NotNull EntityType entity) {
        return Component.translatable(getTranslationKey(entity));
    }
}
