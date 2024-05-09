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
public class ConfigInventoryUI {

    protected String name;

    protected String[] slots;

    protected LinkedHashMap<String, String> dictionary;

    protected LinkedHashMap<String, DefinedConfigItem> items;

    @NotNull
    public InventoryUI build(@NotNull AlpinePlugin plugin, @NotNull UIHandler handler) {
        return new InventoryUI(this, plugin, handler);
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String[] slots;
        private LinkedHashMap<String, String> dictionary;
        private final LinkedHashMap<String, DefinedConfigItem> items = new LinkedHashMap<>();

        @NotNull
        public Builder name(@NotNull String name) {
            Validate.notNull(name, "name cannot be null");
            this.name = name;
            return this;
        }

        @NotNull
        public Builder slots(@NotNull String... slots) {
            Validate.notNull(slots, "slots cannot be null");
            this.slots = slots;
            return this;
        }

        @NotNull
        public Builder dictionary(@NotNull String... dictionary) {
            Validate.notNull(dictionary, "dictionary cannot be null");
            Validate.isTrue(dictionary.length >= 2, "dictionary must have at least 2 entries");
            this.dictionary = CollectionUtils.linkedMap(dictionary);
            return this;
        }

        @NotNull
        public Builder dictionary(@NotNull Map<String, String> dictionary) {
            Validate.notNull(dictionary, "dictionary cannot be null");
            this.dictionary = new LinkedHashMap<>(dictionary);
            return this;
        }

        @NotNull
        public Builder item(@NotNull String key, @NotNull DefinedConfigItem item) {
            Validate.notNull(key, "key cannot be null");
            Validate.notNull(item, "item cannot be null");
            this.items.put(key, item);
            return this;
        }

        @NotNull
        public Builder item(@NotNull String key, @NotNull ItemStack item) {
            Validate.notNull(key, "key cannot be null");
            Validate.notNull(item, "item cannot be null");
            return this.item(key, DefinedConfigItem.fromItem(item));
        }

        @NotNull
        public ConfigInventoryUI build() {
            return new ConfigInventoryUI(this.name, this.slots, this.dictionary, this.items);
        }
    }
}

