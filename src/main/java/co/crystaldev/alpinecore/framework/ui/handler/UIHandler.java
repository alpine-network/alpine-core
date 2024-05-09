package co.crystaldev.alpinecore.framework.ui.handler;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public interface UIHandler {
    @NotNull
    ItemStack createItem(@NotNull String key);
}
