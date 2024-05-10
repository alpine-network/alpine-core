package co.crystaldev.alpinecore.framework.ui.handler;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.type.GenericUIElement;
import co.crystaldev.alpinecore.framework.ui.SlotPosition;
import co.crystaldev.alpinecore.framework.ui.element.UIElement;
import co.crystaldev.alpinecore.framework.ui.event.UIEventSubscriber;
import co.crystaldev.alpinecore.framework.ui.type.ConfigInventoryUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The backbone for user interfaces.
 *
 * @since 0.4.0
 */
public abstract class UIHandler implements UIEventSubscriber {

    /**
     * Initializes the UI context with the given UI context object.
     *
     * @param context the UI context object to initialize
     */
    public void init(@NotNull UIContext context) {
        // NO OP
    }

    /**
     * Handles the event when a user interface is closed.
     *
     * @param context the UIContext object representing the state of the user interface
     */
    public void closed(@NotNull UIContext context) {
        // NO OP
    }

    /**
     * Fills the UI context with elements.
     *
     * @param context the UI context to fill
     */
    public void fill(@NotNull UIContext context) {
        ConfigInventoryUI properties = context.ui().getProperties();
        Map<String, String> dictionary = properties.getDictionary();
        String[] slots = properties.getSlots();

        for (int y = 0; y < slots.length; y++) {
            String row = slots[y];
            for (int x = 0; x < row.length(); x++) {
                char symbol = row.charAt(x);
                if (symbol == ' ') {
                    continue;
                }

                String key = dictionary.get(Character.toString(symbol));
                if (key != null) {
                    UIElement element = this.populateEntry(context, key);
                    element.setPosition(SlotPosition.from(context.inventory(), x, y));
                    context.addElement(element);
                }
            }
        }
    }

    @Nullable
    public abstract UIElement getEntry(@NotNull UIContext context, @NotNull String key);

    @NotNull
    public final UIElement populateEntry(@NotNull UIContext context, @NotNull String key) {
        ConfigInventoryUI properties = context.ui().getProperties();
        Map<String, DefinedConfigItem> dictionary = properties.getItems();

        UIElement fallback = this.getEntry(context, key);
        if (fallback != null) {
            return fallback;
        }
        else if (dictionary.containsKey(key)) {
            DefinedConfigItem configItem = dictionary.get(key);
            UIElement element = new GenericUIElement(context, configItem.build(context.manager().getPlugin()));
            element.setAttributes(configItem.getAttributes());
            return element;
        }
        else {
            // the dictionary does not define the requested item
            ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Undefined Element");
            itemStack.setItemMeta(itemMeta);

            return new GenericUIElement(context, itemStack);
        }
    }
}
