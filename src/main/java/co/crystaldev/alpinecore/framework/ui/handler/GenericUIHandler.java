package co.crystaldev.alpinecore.framework.ui.handler;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public final class GenericUIHandler implements UIHandler {

    private static final GenericUIHandler INSTANCE = new GenericUIHandler();

    @Override
    public @NotNull ItemStack createItem(@NotNull String key) {
        return null;
    }

    @NotNull
    public static GenericUIHandler getInstance() {
        return INSTANCE;
    }
}
