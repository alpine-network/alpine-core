package co.crystaldev.alpinecore.framework.ui.type;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.handler.UIHandler;
import co.crystaldev.alpinecore.util.CollectionUtils;
import com.google.common.annotations.Beta;
import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.4.0
 */
@AllArgsConstructor @NoArgsConstructor @Getter
@Configuration
@Beta
public final class ConfigInventoryUI {

    private String name;

    private String[] slots;

    private LinkedHashMap<String, String> dictionary;

    private LinkedHashMap<String, DefinedConfigItem> items;

    /**
     * Retrieves the number of occurrences for a symbol in the slots of the InventoryUI.
     *
     * @param key The symbol to search for.
     * @return The number of occurrences for the symbol in the slots. Returns 0 if the symbol is not found.
     */
    public int countSymbol(@NotNull String key) {
        String symbol = null;
        for (Map.Entry<String, String> entry : this.dictionary.entrySet()) {
            if (entry.getValue().equals(key)) {
                symbol = entry.getKey();
                break;
            }
        }

        if (symbol == null) {
            return 0;
        }

        int amount = 0;
        for (String slot : this.slots) {
            for (char ch : slot.toCharArray()) {
                if (ch == symbol.charAt(0)) {
                    amount++;
                }
            }
        }

        return amount;
    }

    public @NotNull InventoryUI build(@NotNull AlpinePlugin plugin, @NotNull UIHandler handler) {
        return new InventoryUI(this, plugin, handler);
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String[] slots;
        private LinkedHashMap<String, String> dictionary;
        private final LinkedHashMap<String, DefinedConfigItem> items = new LinkedHashMap<>();

        public @NotNull Builder name(@NotNull String name) {
            Validate.notNull(name, "name cannot be null");
            this.name = name;
            return this;
        }

        public @NotNull Builder slots(@NotNull String... slots) {
            Validate.notNull(slots, "slots cannot be null");
            this.slots = slots;
            return this;
        }

        public @NotNull Builder dictionary(@NotNull String... dictionary) {
            Validate.notNull(dictionary, "dictionary cannot be null");
            Validate.isTrue(dictionary.length >= 2, "dictionary must have at least 2 entries");
            this.dictionary = CollectionUtils.linkedMap(dictionary);
            return this;
        }

        public @NotNull Builder dictionary(@NotNull Map<String, String> dictionary) {
            Validate.notNull(dictionary, "dictionary cannot be null");
            this.dictionary = new LinkedHashMap<>(dictionary);
            return this;
        }

        public @NotNull Builder item(@NotNull String key, @NotNull DefinedConfigItem item) {
            Validate.notNull(key, "key cannot be null");
            Validate.notNull(item, "item cannot be null");
            this.items.put(key, item);
            return this;
        }

        public @NotNull Builder item(@NotNull String key, @NotNull ItemStack item) {
            Validate.notNull(key, "key cannot be null");
            Validate.notNull(item, "item cannot be null");
            return this.item(key, DefinedConfigItem.fromItem(item));
        }

        public @NotNull ConfigInventoryUI build() {
            Validate.notNull(this.name, "name cannot be null");
            Validate.notNull(this.slots, "slots cannot be null");
            Validate.notNull(this.dictionary, "dictionary cannot be null");
            Validate.notNull(this.items, "items cannot be null");
            return new ConfigInventoryUI(this.name, this.slots, this.dictionary, this.items);
        }
    }
}

