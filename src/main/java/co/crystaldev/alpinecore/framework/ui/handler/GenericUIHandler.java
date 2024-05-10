package co.crystaldev.alpinecore.framework.ui.handler;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.event.UIEventPriority;
import co.crystaldev.alpinecore.framework.ui.event.type.ClickEvent;
import co.crystaldev.alpinecore.framework.ui.event.type.DragEvent;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.4.0
 */
public class GenericUIHandler extends UIHandler {

    private static final GenericUIHandler INSTANCE = new GenericUIHandler();

    @NotNull
    public static GenericUIHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable Element createEntry(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem dictionaryDefinition) {
        return null;
    }

    @Override
    public void registerEvents(@NotNull UIContext context, @NotNull UIEventBus bus) {
        // Do not allow dragging items in the inventory
        bus.register(DragEvent.class, UIEventPriority.LOWEST, (ctx, event) -> {
            return ActionResult.CANCEL;
        });

        // Do not allow moving items in the inventory
        bus.register(ClickEvent.class, UIEventPriority.LOWEST, (ctx, event) -> {
            InventoryClickEvent handle = event.getHandle();
            if (!handle.getInventory().equals(ctx.inventory())) {
                return ActionResult.PASS;
            }

            // Player clicked an element inside our inventory
            if (handle.getClickedInventory() != null && handle.getClickedInventory().equals(ctx.inventory())) {
                int mouseButton;
                switch (handle.getClick()) {
                    case LEFT:
                        mouseButton = 0;
                        break;
                    case RIGHT:
                        mouseButton = 1;
                        break;
                    case MIDDLE:
                        mouseButton = 2;
                        break;
                    default:
                        return ActionResult.CANCEL;
                }

                context.getElements().forEach(element -> {
                    if (element.getPosition().getSlot() == handle.getSlot()) {
                        element.clicked(mouseButton);
                    }
                });
            }

            return ActionResult.CANCEL;
        });
    }
}
