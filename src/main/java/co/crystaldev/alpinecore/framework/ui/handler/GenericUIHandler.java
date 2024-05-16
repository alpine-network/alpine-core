package co.crystaldev.alpinecore.framework.ui.handler;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.interaction.ClickContext;
import co.crystaldev.alpinecore.framework.ui.interaction.ClickProperties;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.event.UIEventPriority;
import co.crystaldev.alpinecore.framework.ui.event.type.ClickEvent;
import co.crystaldev.alpinecore.framework.ui.event.type.DragEvent;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.interaction.DropContext;
import org.bukkit.Material;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
    public @Nullable Element createElement(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem definition) {
        return null;
    }

    @Override
    public void registerEvents(@NotNull UIEventBus bus) {
        bus.register(DragEvent.class, UIEventPriority.LOWEST, (ctx, event) -> {
            InventoryDragEvent handle = event.getHandle();

            ClickType type = handle.getType() == DragType.EVEN ? ClickType.LEFT : ClickType.RIGHT;
            InventoryView view = handle.getView();
            Inventory top = view.getTopInventory();

            ActionResult result = ActionResult.PASS;

            Map<Integer, ItemStack> items = handle.getNewItems();
            for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                int index = entry.getKey();
                ItemStack item = entry.getValue();

                if (index >= top.getSize()) {
                    continue;
                }

                InventoryAction action = item.getAmount() > 1 ? InventoryAction.PLACE_SOME : InventoryAction.PLACE_ONE;
                ClickContext handledClick = handleClick(ctx, index, type, action, item);
                if (handledClick != null && handledClick.result() != ActionResult.PASS) {
                    result = handledClick.result();
                }
            }

            return result;
        });

        bus.register(ClickEvent.class, UIEventPriority.LOWEST, (ctx, event) -> {
            InventoryClickEvent handle = event.getHandle();
            if (!handle.getInventory().equals(ctx.inventory())) {
                return ActionResult.PASS;
            }

            InventoryView view = handle.getView();
            Inventory top = view.getTopInventory();
            Inventory clicked = handle.getClickedInventory();

            ClickType type = handle.getClick();
            InventoryAction action = handle.getAction();

            ActionResult result = ActionResult.PASS;

            switch (action) {
                case NOTHING:
                case UNKNOWN:
                    result = ActionResult.SUCCESS;
                    break;

                case DROP_ALL_CURSOR:
                case DROP_ONE_CURSOR:
                    DropContext dropContext = new DropContext(type, action, handle.getCursor(), result);

                    ctx.ui().getHandler().dropped(ctx, dropContext);

                    result = dropContext.result();
                    break;

                case COLLECT_TO_CURSOR:
                    if (!top.equals(clicked)) {
                        ItemStack moving = handle.getCursor();
                        Material cursorType = moving.getType();
                        int maxSize = cursorType.getMaxStackSize();
                        int remainingAmount = maxSize - moving.getAmount();

                        for (int i = 0; i < top.getSize(); i++) {
                            ItemStack item = top.getItem(i);
                            if (item == null || !item.isSimilar(moving)) {
                                continue;
                            }

                            int slotAmount = Math.min(item.getAmount(), remainingAmount);
                            remainingAmount -= slotAmount;

                            ItemStack adding = new ItemStack(moving);
                            adding.setAmount(slotAmount);

                            ClickContext handledClick = handleClick(ctx, i, type, action, null);
                            if (handledClick != null && handledClick.result() != ActionResult.PASS) {
                                result = handledClick.result();
                                break;
                            }

                            if (remainingAmount <= 0) {
                                break;
                            }
                        }
                    }
                    break;

                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                case HOTBAR_MOVE_AND_READD:
                case HOTBAR_SWAP:
                case SWAP_WITH_CURSOR:
                case PLACE_ALL:
                case PLACE_SOME:
                case PLACE_ONE:
                case PICKUP_ALL:
                case PICKUP_SOME:
                case PICKUP_HALF:
                case PICKUP_ONE:
                    if (top.equals(clicked)) {
                        ActionResult handledClick = handleClick(ctx, handle, handle.getSlot());
                        if (handledClick != ActionResult.PASS) {
                            result = handledClick;
                        }
                    }
                    break;

                case MOVE_TO_OTHER_INVENTORY:
                    if (top.equals(clicked)) {
                        ActionResult handledClick = handleClick(ctx, handle, handle.getSlot());
                        if (handledClick != ActionResult.PASS) {
                            result = handledClick;
                        }
                        break;
                    }

                    result = handleShiftClick(ctx, handle, top, type, action, result);
            }

            return result;
        });
    }

    @NotNull
    private static ActionResult handleClick(@NotNull UIContext context, @NotNull InventoryClickEvent event, int slot) {
        ClickContext clickContext = handleClick(context, slot, event.getClick(), event.getAction(), event.getCursor());
        if (clickContext == null) {
            return ActionResult.PASS;
        }

        if (clickContext.consumedItem()) {
            event.setCursor(null);
        }

        return clickContext.result();
    }

    @Nullable
    private static ClickContext handleClick(@NotNull UIContext context, int slot, @NotNull ClickType type,
                                            @NotNull InventoryAction action, @Nullable ItemStack clicked) {
        ActionResult result = ActionResult.PASS;

        for (Element element : context.getElements()) {
            if (element.getPosition().getSlot() != slot) {
                continue;
            }

            ClickProperties properties = element.getClickProperties();
            if (!properties.isAllowed(type) || !properties.isAllowed(action)) {
                result = ActionResult.CANCEL;
            }

            ClickContext clickContext = new ClickContext(type, action, clicked, result);
            element.clicked(clickContext);
            return clickContext;
        }

        return null;
    }

    @NotNull
    private static ActionResult handleShiftClick(@NotNull UIContext context, @NotNull InventoryClickEvent event,
                                                 @NotNull Inventory top, @NotNull ClickType type,
                                                 @NotNull InventoryAction action, @NotNull ActionResult result) {
        ItemStack moving = event.getCurrentItem();
        int remainingAmount = moving.getAmount();
        int nextSlot = findNextEmptySlot(top, moving, 0);

        while (nextSlot > -1 && remainingAmount > 0 && result == ActionResult.PASS) {
            ItemStack item = top.getItem(nextSlot);

            int slotAmount;
            if (item == null || item.getType() == Material.AIR) {
                slotAmount = moving.getMaxStackSize();
            }
            else {
                slotAmount = item.getMaxStackSize() - item.getAmount();
            }

            slotAmount = Math.min(slotAmount, remainingAmount);
            remainingAmount -= slotAmount;

            ItemStack adding = new ItemStack(moving);
            adding.setAmount(slotAmount);

            ClickContext handledClick = handleClick(context, nextSlot, type, action, adding);
            if (handledClick != null && handledClick.result() != ActionResult.PASS) {
                result = handledClick.result();
            }

            if (handledClick != null && handledClick.consumedItem()) {
                event.setCursor(null);
            }

            nextSlot = findNextEmptySlot(top, moving, nextSlot + 1);
        }

        return result;
    }

    private static int findNextEmptySlot(@NotNull Inventory inventory, @NotNull ItemStack moving, int startIndex) {
        for (int i = startIndex ; i < inventory.getSize() ; i++) {
            ItemStack item = inventory.getItem(i);
            if (moving.isSimilar(item) && item.getAmount() < item.getMaxStackSize()) {
                return i;
            }
        }
        return inventory.firstEmpty();
    }
}
