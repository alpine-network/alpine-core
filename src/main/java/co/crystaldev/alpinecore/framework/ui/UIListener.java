package co.crystaldev.alpinecore.framework.ui;

import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import co.crystaldev.alpinecore.framework.ui.event.type.DragEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 05/08/2024
 */
@RequiredArgsConstructor
final class UIListener implements Listener {

    private final AlpineUIManager manager;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        if (this.manager.isManaged(player.getUniqueId())) {
            this.manager.close(player.getUniqueId(), false);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (!this.manager.isManaged(player.getUniqueId())) {
            return;
        }


    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (!this.manager.isManaged(player.getUniqueId())) {
            return;
        }

        UIContext context = this.manager.getState(player.getUniqueId());
        ActionResult result = context.eventBus().call(context, new DragEvent(event));
        if (result == ActionResult.CANCEL) {
            event.setCancelled(true);
        }
    }
}
