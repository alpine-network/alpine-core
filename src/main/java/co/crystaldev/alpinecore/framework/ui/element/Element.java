package co.crystaldev.alpinecore.framework.ui.element;

import co.crystaldev.alpinecore.framework.ui.ClickFunction;
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
    protected ClickFunction onClick;

    @Getter @Setter
    protected ClickProperties clickProperties = ClickProperties.ALL_DISALLOWED;

    public Element(@NotNull UIContext context) {
        this.context = context;
    }

    /**
     * Builds an ItemStack representing this UI element.
     *
     * @return the ItemStack representing this UI element, or null if there is no item stack
     */
    @Nullable
    public abstract ItemStack buildItemStack();

    /**
     * Initializes the UI element.
     */
    public void init() {
        // NO OP
    }

    /**
     * Called when the UI element is clicked.
     *
     * @param context the click context for the interaction
     */
    public void clicked(@NotNull ClickContext context) {
        if (this.onClick != null) {
            this.onClick.mouseClicked(this.context, context);
        }
    }

    /**
     * Checks for permission to transfer items in user's inventory.
     *
     * @return true if transfer is permitted, false otherwise
     */
    public boolean canTransferItems() {
        return false;
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
    public void registerEvents(@NotNull UIEventBus bus) {
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
