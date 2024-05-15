package dev.tomwmth.exampleplugin.ui;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.element.ElementPaginator;
import co.crystaldev.alpinecore.framework.ui.element.ElementProvider;
import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.event.type.DragEvent;
import co.crystaldev.alpinecore.framework.ui.handler.GenericUIHandler;
import co.crystaldev.alpinecore.util.MappedMaterial;
import com.cryptomorin.xseries.XMaterial;
import dev.tomwmth.exampleplugin.config.Config;
import org.apache.commons.lang.WordUtils;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.4.0
 */
public final class DemoUIHandler extends GenericUIHandler {

    private static final MappedMaterial BOATS_AND_DOORS = MappedMaterial.builder()
            .add(materials -> materials.filter(type -> type.name().endsWith("_BOAT") || type.name().endsWith("_DOOR")))
            .build();

    private static final DemoUIHandler INSTANCE = new DemoUIHandler();

    @NotNull
    public static DemoUIHandler getInstance() {
        return INSTANCE;
    }

    private final ElementPaginator<XMaterial> paginator = ElementPaginator.<XMaterial>builder()
            .elementProvider(
                    ElementProvider.<XMaterial, Element>builder()
                            .entries(BOATS_AND_DOORS.getMaterials())
                            .element((ctx, type) -> {
                                AlpinePlugin plugin = ctx.plugin();
                                Config config = plugin.getConfigManager().getConfig(Config.class);

                                return Element.of(ctx, config.stockSelectionItem.define(type).build(plugin,
                                        "item_name", WordUtils.capitalizeFully(type.name().replace('_', ' ')),
                                        "price", 50
                                ));
                            })
                            .build()
            )
            .emptySlotProvider(ctx -> {
                AlpinePlugin plugin = ctx.plugin();
                Config config = plugin.getConfiguration(Config.class);
                return config.stockPlaceholderItem.build(plugin);
            })
            .build();

    @Override
    public void closed(@NotNull UIContext context) {
        super.closed(context);

        this.paginator.closed(context);
    }

    @Override
    public @Nullable Element createEntry(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem definition) {
        switch (key) {
            case "stock-selection":
                return this.paginator.buildNextSlot(context);
            case "previous-page":
                if (definition == null) {
                    return null;
                }
                return this.paginator.buildPreviousNav(context, definition);
            case "next-page":
                if (definition == null) {
                    return null;
                }
                return this.paginator.buildNextNav(context, definition);
            case "page-info":
                if (definition == null) {
                    return null;
                }
                return this.paginator.buildNavInfo(context, definition);
            default:
                return null;
        }
    }

    @Override
    public void registerEvents(@NotNull UIEventBus bus) {
        super.registerEvents(bus);

        bus.register(DragEvent.class, (ctx, event) -> {
            InventoryDragEvent handle = event.getHandle();
            System.out.println(handle.getInventory().equals(ctx.inventory()));

            return ActionResult.PASS;
        });
    }
}
