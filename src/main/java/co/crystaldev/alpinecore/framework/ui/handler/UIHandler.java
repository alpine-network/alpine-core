package co.crystaldev.alpinecore.framework.ui.handler;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.type.GenericElement;
import co.crystaldev.alpinecore.framework.ui.SlotPosition;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.event.UIEventSubscriber;
import co.crystaldev.alpinecore.framework.ui.interaction.DropContext;
import co.crystaldev.alpinecore.framework.ui.type.ConfigInventoryUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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
     * Handles the event when an item is dropped from the mouse cursor.
     *
     * @param context the UI context
     * @param drop    the drop context
     */
    public void dropped(@NotNull UIContext context, @NotNull DropContext drop) {
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
                    Element element = this.populateElement(context, key);
                    element.setPosition(SlotPosition.from(context.inventory(), x, y));
                    context.addElement(element);
                }
            }
        }
    }

    /**
     * Creates an element for the UI context with the given key and dictionary definition.
     *
     * @param context    the UI context in which the entry is created
     * @param key        the key for the entry
     * @param definition the dictionary definition for the entry
     *
     * @return the created Element instance, or null if the entry cannot be created
     */
    @Nullable
    public abstract Element createElement(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem definition);

    /**
     * Populates an element in the UI context with the given key.
     *
     * @param context the UI context in which to populate the entry
     * @param key     the key for the entry to populate
     * @return the populated Element instance, or null if the entry cannot be populated
     */
    @NotNull
    public final Element populateElement(@NotNull UIContext context, @NotNull String key) {
        ConfigInventoryUI properties = context.ui().getProperties();
        Map<String, DefinedConfigItem> dictionary = properties.getItems();

        Element entry = this.createElement(context, key, dictionary.get(key));
        if (entry != null) {
            return entry;
        }
        else if (dictionary.containsKey(key)) {
            DefinedConfigItem configItem = dictionary.get(key);
            Element element = new GenericElement(context, configItem.build(context.manager().getPlugin()));
            element.putAttributes(Optional.ofNullable(configItem.getAttributes()).orElse(Collections.emptyMap()));
            return element;
        }
        else {
            // the dictionary does not define the requested item
            ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Undefined Element: " + key);
            itemStack.setItemMeta(itemMeta);

            return new GenericElement(context, itemStack);
        }
    }
}
