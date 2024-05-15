package co.crystaldev.alpinecore.framework.ui.element;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the properties of a click action in an inventory.
 *
 * @since 0.4.0
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClickProperties {

    public static final ClickProperties ALL_ALLOWED = builder()
            .actions(InventoryAction.values()).types(ClickType.values()).build();

    public static final ClickProperties ALL_DISALLOWED = builder().build();

    private final Set<InventoryAction> allowedActions;

    private final Set<ClickType> allowedTypes;

    /**
     * Checks if the provided InventoryAction is allowed.
     *
     * @param action the InventoryAction to check
     * @return true if the action is allowed, false otherwise
     */
    public boolean isAllowed(@NotNull InventoryAction action) {
        return this.allowedActions.contains(action);
    }

    /**
     * Checks if the provided ClickType is allowed.
     *
     * @param type the ClickType to check
     * @return true if the ClickType is allowed, false otherwise
     */
    public boolean isAllowed(@NotNull ClickType type) {
        return this.allowedTypes.contains(type);
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @since 0.4.0
     */
    public static final class Builder {

        private final Set<InventoryAction> allowedActions = new HashSet<>();
        private final Set<ClickType> allowedTypes = new HashSet<>();

        @NotNull
        public Builder action(@NotNull InventoryAction action) {
            Validate.notNull(action, "action cannot be null");
            this.allowedActions.add(action);
            return this;
        }

        @NotNull
        public Builder actions(@NotNull InventoryAction... actions) {
            for (InventoryAction action : actions) {
                this.action(action);
            }
            return this;
        }

        @NotNull
        public Builder type(@NotNull ClickType type) {
            Validate.notNull(type, "type cannot be null");
            this.allowedTypes.add(type);
            return this;
        }

        @NotNull
        public Builder types(@NotNull ClickType... types) {
            for (ClickType type : types) {
                this.type(type);
            }
            return this;
        }

        @NotNull
        public ClickProperties build() {
            return new ClickProperties(this.allowedActions, this.allowedTypes);
        }
    }
}
