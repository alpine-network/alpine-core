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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a configurable item within the plugin framework.
 * Provides methods to build and manipulate item stacks based on dynamic configurations.
 *
 * @since 0.4.0
 */
@NoArgsConstructor @AllArgsConstructor @Getter
@Configuration @SerializeWith(serializer = ConfigItemAdapter.class)
public class VaryingConfigItem implements ConfigItem {

    protected String name;

    protected List<String> lore;

    protected int count = -1;

    protected boolean enchanted;

    @NotNull
    public DefinedConfigItem define(@NotNull XMaterial type) {
        return new DefinedConfigItem(type, this.name, this.lore, this.count, this.enchanted);
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private List<String> lore;
        private int count = -1;
        private boolean enchanted;

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
        public VaryingConfigItem build() {
            String name = this.name == null ? "" : this.name;
            List<String> lore = this.lore == null ? Collections.emptyList() : this.lore ;
            return new VaryingConfigItem(name, lore, this.count, this.enchanted);
        }
    }
}