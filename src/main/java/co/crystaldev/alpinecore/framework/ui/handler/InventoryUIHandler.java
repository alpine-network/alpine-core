package co.crystaldev.alpinecore.framework.ui.handler;

import co.crystaldev.alpinecore.framework.ui.element.ElementProvider;
import co.crystaldev.alpinecore.framework.ui.element.UIElement;
import co.crystaldev.alpinecore.framework.ui.element.type.GenericUIElement;
import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.event.UIEventPriority;
import co.crystaldev.alpinecore.framework.ui.event.type.ClickEvent;
import co.crystaldev.alpinecore.framework.ui.event.type.DragEvent;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 0.4.0
 */
public class InventoryUIHandler extends UIHandler {

    private static final InventoryUIHandler INSTANCE = new InventoryUIHandler();

    private final ElementProvider<XMaterial, GenericUIElement> materialProvider = ElementProvider.<XMaterial, GenericUIElement>builder()
            .entries(Stream.of(XMaterial.values()).filter(XMaterial::isSupported).collect(Collectors.toList()))
            .element((ctx, type) -> new GenericUIElement(ctx, type.parseItem()))
            .build();

    @Override
    public void init(@NotNull UIContext context) {
        this.materialProvider.init(context);
    }

    @Override
    public @Nullable UIElement getEntry(@NotNull UIContext context, @NotNull String key) {
        switch (key) {
            case "stock-selection":
                return UIElement.fromNullable(this.materialProvider.nextElement(context));
            case "previous-page":
                break;
            case "next-page":
                break;
            case "page-info":
                break;
        }

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
            if (handle.getInventory().equals(ctx.inventory())) {
                return ActionResult.CANCEL;
            }

            return ActionResult.PASS;
        });
    }

    @NotNull
    public static InventoryUIHandler getInstance() {
        return INSTANCE;
    }
}
