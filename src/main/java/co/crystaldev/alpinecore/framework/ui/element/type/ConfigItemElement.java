package co.crystaldev.alpinecore.framework.ui.element.type;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Represents a UI element wrapping a {@link DefinedConfigItem}.
 *
 * @since 0.4.0
 */
public final class ConfigItemElement extends Element {

    private final DefinedConfigItem configItem;

    private final Object[] placeholders;

    public ConfigItemElement(@NotNull UIContext context, @NotNull DefinedConfigItem configItem,
                             @Nullable Object[] placeholders) {
        super(context);
        this.configItem = configItem;
        this.placeholders = placeholders;

        Map<String, Object> attributes = configItem.getAttributes();
        if (attributes != null) {
            this.putAttributes(attributes);
        }
    }

    @Override
    public @NotNull ItemStack buildItemStack() {
        return this.configItem.build(this.context.plugin(), this.placeholders);
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * The Builder class is used to create instances of {@link ConfigItemElement}.
     * It provides methods to set the type of the config item, add placeholders, and build the element.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ConfigItemElement element = ConfigItemElement.builder()
     *      .type(configItem)
     *      .placeholder("key1", value1)
     *      .placeholders("key2", value2, "key3", value3)
     *      .build(uiContext);
     * }</pre>
     *
     * <p>Note: The {@code Builder} class is a static nested class of {@code ConfigItemElement}.
     * To use the builder, call the {@link ConfigItemElement#builder()} method.</p>
     *
     * @since 0.4.0
     */
    public static final class Builder {

        private DefinedConfigItem item;
        private final Map<String, Object> placeholders = new HashMap<>();

        public @NotNull Builder type(@NotNull DefinedConfigItem item) {
            Validate.notNull(item, "item cannot be null");
            this.item = item;
            return this;
        }

        public @NotNull Builder placeholder(@NotNull String key, @NotNull Object value) {
            Validate.notNull(key, "key cannot be null");
            this.placeholders.put(key, value);
            return this;
        }

        public @NotNull Builder placeholder(@NotNull String key, @NotNull Supplier<?> value) {
            Validate.notNull(key, "key cannot be null");
            this.placeholders.put(key, value);
            return this;
        }

        public @NotNull Builder placeholders(@NotNull Object... placeholders) {
            Validate.notNull(placeholders, "placeholders cannot be null");
            Validate.isTrue(placeholders.length % 2 == 0, "placeholders must be even");
            for (int i = 0; i < (placeholders.length / 2) * 2; i += 2) {
                String key = placeholders[i].toString();
                Object value = placeholders[i + 1];
                this.placeholders.put(key, value);
            }
            return this;
        }

        public @NotNull Builder placeholders(@NotNull Map<String, Object> placeholders) {
            this.placeholders.putAll(placeholders);
            return this;
        }

        public @NotNull ConfigItemElement build(@NotNull UIContext context) {
            Object[] placeholders = new Object[this.placeholders.size() * 2];
            AtomicInteger index = new AtomicInteger();
            this.placeholders.forEach((key, value) -> {
                int i = index.get();
                placeholders[i] = key;
                placeholders[i + 1] = value;
                index.set(i + 2);
            });

            return new ConfigItemElement(context, this.item, placeholders);
        }
    }
}
