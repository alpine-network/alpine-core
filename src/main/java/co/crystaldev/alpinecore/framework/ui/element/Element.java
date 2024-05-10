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
 * Represents an abstract UI Element.
 *
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

    /**
     * Initializes the UI element.
     */
    public abstract void init();

    /**
     * Builds an ItemStack representing this UI element.
     *
     * @return the ItemStack representing this UI element, or null if there is no item stack
     */
    @Nullable
    public abstract ItemStack buildItemStack();

    /**
     * Handle the click event on the UI element.
     *
     * @param mouseButton the mouse button that was clicked
     */
    public void clicked(int mouseButton) {
        if (this.onClick != null) {
            this.onClick.mouseClicked(mouseButton);
        }
    }

    /**
     * Retrieves the attribute with the specified key.
     *
     * @param <T> the type of the attribute value
     *
     * @param key the key of the attribute to retrieve
     * @return the attribute value associated with the key, or null if the attribute does not exist
     */
    @Nullable
    public <T> T getAttribute(@NotNull String key) {
        return this.attributes == null ? null : (T) this.attributes.get(key);
    }

    /**
     * Sets an attribute with the specified key and value for the Element.
     *
     * @param key   the key of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(@NotNull String key, @Nullable Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    /**
     * Retrieves the attributes associated with this Element.
     *
     * @return a map of attribute keys to attribute values
     */
    @NotNull
    public Map<String, Object> getAttributes() {
        return this.attributes == null ? Collections.emptyMap() : this.attributes;
    }

    /**
     * Sets the attributes with the specified key-value pairs for the Element.
     *
     * @param attributes a map of attribute keys to attribute values
     */
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
