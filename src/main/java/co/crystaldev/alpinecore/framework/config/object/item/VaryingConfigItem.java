package co.crystaldev.alpinecore.framework.config.object.item;

import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a configurable item within the plugin framework.
 * Provides methods to build and manipulate item stacks based on dynamic configurations.
 *
 * @since 0.4.0
 */
@NoArgsConstructor @AllArgsConstructor @Getter
@Configuration @SerializeWith(serializer = VaryingConfigItem.Adapter.class)
public class VaryingConfigItem implements ConfigItem {

    protected String name;

    protected List<String> lore;

    protected int count = -1;

    protected boolean enchanted;

    protected Map<String, Object> attributes;

    @NotNull
    public DefinedConfigItem define(@NotNull XMaterial type) {
        return new DefinedConfigItem(type, this.name, this.lore, this.count, this.enchanted, this.attributes);
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The Builder class is used to construct instances of the VaryingConfigItem class.
     * It provides methods to set the name, lore, count, enchanted status, and attributes of the item.
     * <br>
     * Example usage:
     * <pre>{@code
     * Builder builder = VaryingConfigItem.builder();
     * VaryingConfigItem item = builder
     *      .name("<info>Varying Item")
     *      .lore(
     *          "These are some nifty lore lines!",
     *          "",
     *          "<info>  *</info> <emphasis>Type: %type%"
     *      )
     *      .count(64)
     *      .enchanted()
     *      .attribute("key", "value");
     *
     * XMaterial type = XMaterial.STICK;
     * ItemStack builtItem = item.build(plugin,
     *      type, // You need to plug the type in here
     *      "type", type);
     * }</pre>
     */
    public static final class Builder {

        private String name;
        private List<String> lore;
        private int count = -1;
        private boolean enchanted;
        private Map<String, Object> attributes;

        @NotNull
        public Builder name(@NotNull String name) {
            Validate.notNull(name, "name cannot be null");
            this.name = name;
            return this;
        }

        @NotNull
        public Builder lore(@NotNull String... lore) {
            Validate.notNull(lore, "lore cannot be null");
            List<String> processedLore = new LinkedList<>();
            for (String s : lore) {
                processedLore.addAll(Arrays.asList(s.split("(\n|<br>)")));
            }
            this.lore = processedLore;
            return this;
        }

        @NotNull
        public Builder lore(@NotNull Iterable<String> lore) {
            Validate.notNull(lore, "lore cannot be null");
            List<String> processedLore = new LinkedList<>();
            for (String s : lore) {
                processedLore.addAll(Arrays.asList(s.split("(\n|<br>)")));
            }
            this.lore = processedLore;
            return this;
        }

        @NotNull
        public Builder lore(@NotNull Component lore) {
            Validate.notNull(lore, "lore cannot be null");
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            String serialized = serializer.serialize(lore);
            this.lore = new LinkedList<>(Arrays.asList(serialized.split("(\n|<br>)")));
            return this;
        }

        @NotNull
        public Builder count(int count) {
            this.count = count;
            return this;
        }

        @NotNull
        public Builder enchanted() {
            this.enchanted = true;
            return this;
        }

        @NotNull
        public Builder attribute(@NotNull String key, @Nullable Object value) {
            if (this.attributes == null) {
                if (value == null) {
                    return this;
                }

                this.attributes = new LinkedHashMap<>();
            }

            this.attributes.put(key, value);
            return this;
        }

        @NotNull
        public Builder attributes(@NotNull Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        @NotNull
        public VaryingConfigItem build() {
            String name = this.name == null ? "" : this.name;
            List<String> lore = this.lore == null ? Collections.emptyList() : this.lore ;
            return new VaryingConfigItem(name, lore, this.count, this.enchanted, this.attributes);
        }
    }

    static final class Adapter extends ConfigItemAdapter {
        @Override
        public boolean requiresType() {
            return false;
        }
    }
}