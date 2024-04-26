package co.crystaldev.alpinecore.framework.config.object.item;

import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Configuration
public class VaryingConfigItem implements ConfigItem {

    private String name;

    private List<String> lore;

    private boolean enchanted;

    @NotNull
    public DefinedConfigItem define(@NotNull XMaterial type) {
        return new DefinedConfigItem(type, this.name, this.lore, this.enchanted);
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private List<String> lore;
        private boolean enchanted;

        @NotNull
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        @NotNull
        public Builder lore(@NotNull String... lore) {
            List<String> processedLore = new LinkedList<>();
            for (String s : lore) {
                processedLore.addAll(Arrays.asList(s.split("(\n|<br>)")));
            }
            this.lore = processedLore;
            return this;
        }

        @NotNull
        public Builder enchanted() {
            this.enchanted = true;
            return this;
        }

        @NotNull
        public VaryingConfigItem build() {
            List<String> lore = this.lore == null ? Collections.emptyList() : this.lore ;
            return new VaryingConfigItem(this.name, lore, this.enchanted);
        }
    }
}