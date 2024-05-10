package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.ui.SlotPosition;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.type.EmptyUIElement;
import co.crystaldev.alpinecore.framework.ui.element.type.GenericUIElement;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.event.UIEventSubscriber;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.4.0
 */
public abstract class UIElement implements UIEventSubscriber {

    protected final UIContext context;

    @Getter @Setter
    protected SlotPosition position;

    protected Map<String, Object> attributes;

    public UIElement(UIContext context) {
        this.context = context;
    }

    public abstract void init();

    @Nullable
    public abstract ItemStack buildItemStack();

    @Nullable
    public <T> T getAttribute(@NotNull String key) {
        return (T) this.attributes.get(key);
    }

    public void setAttribute(@NotNull String key, @Nullable Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    @NotNull
    public Map<String, Object> getAttributes() {
        return this.attributes == null ? Collections.emptyMap() : this.attributes;
    }

    public void setAttributes(@Nullable Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void registerEvents(@NotNull UIContext context, @NotNull UIEventBus bus) {
        // NO OP
    }

    @NotNull
    public static GenericUIElement of(@NotNull UIContext context, @NotNull ItemStack itemStack) {
        return new GenericUIElement(context, itemStack);
    }

    @NotNull
    public static UIElement fromNullable(@Nullable UIElement element) {
        return element == null ? empty() : element;
    }

    @NotNull
    public static UIElement empty() {
        return EmptyUIElement.empty();
    }
}
