package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.ui.ClickedFunction;
import co.crystaldev.alpinecore.framework.ui.SlotPosition;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.type.EmptyElement;
import co.crystaldev.alpinecore.framework.ui.element.type.GenericElement;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.event.UIEventSubscriber;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @since 0.4.0
 */
public abstract class Element implements UIEventSubscriber {

    protected final UIContext context;

    @Getter @Setter
    protected SlotPosition position;

    protected Map<String, Object> attributes;

    @Setter
    protected ClickedFunction onClick;

    public Element(@NotNull UIContext context) {
        this.context = context;
    }

    public abstract void init();

    @Nullable
    public abstract ItemStack buildItemStack();

    public void clicked(int mouseButton) {
        if (this.onClick != null) {
            this.onClick.mouseClicked(mouseButton);
        }
    }

    @Nullable
    public <T> T getAttribute(@NotNull String key) {
        return this.attributes == null ? null : (T) this.attributes.get(key);
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

    public void putAttributes(@NotNull Map<String, Object> attributes) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.putAll(attributes);
    }

    @Override
    public void registerEvents(@NotNull UIContext context, @NotNull UIEventBus bus) {
        // NO OP
    }

    public static boolean isEmpty(@Nullable Element element) {
        return element == null || element instanceof EmptyElement;
    }

    @NotNull
    public static GenericElement of(@NotNull UIContext context, @NotNull ItemStack itemStack) {
        return new GenericElement(context, itemStack);
    }

    @NotNull
    public static Element fromNullable(@Nullable Element element) {
        return element == null ? empty() : element;
    }

    @NotNull
    public static Element empty() {
        return EmptyElement.empty();
    }
}
