package dev.tomwmth.exampleplugin.ui;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.element.ElementPaginator;
import co.crystaldev.alpinecore.framework.ui.element.ElementProvider;
import co.crystaldev.alpinecore.framework.ui.element.type.ConfigItemElement;
import co.crystaldev.alpinecore.framework.ui.handler.GenericUIHandler;
import co.crystaldev.alpinecore.util.MappedMaterial;
import com.cryptomorin.xseries.XMaterial;
import dev.tomwmth.exampleplugin.config.Config;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.4.0
 */
public final class DemoUIHandler extends GenericUIHandler {

    private static final MappedMaterial BOATS_DOORS_AND_TRAPDOORS = MappedMaterial.builder()
            .add(materials -> materials.filter(type -> type.name().endsWith("_BOAT") || type.name().endsWith("_DOOR") || type.name().endsWith("_TRAPDOOR")))
            .build();

    private static final DemoUIHandler INSTANCE = new DemoUIHandler();

    public static @NotNull DemoUIHandler getInstance() {
        return INSTANCE;
    }

    private final ElementPaginator<XMaterial> paginator = ElementPaginator.<XMaterial>builder()
            .elementProvider(
                    ElementProvider.<XMaterial, Element>builder()
                            .entries(BOATS_DOORS_AND_TRAPDOORS.getMaterials())
                            .element((ctx, type) -> {
                                AlpinePlugin plugin = ctx.plugin();
                                Config config = plugin.getConfigManager().getConfig(Config.class);

                                return ConfigItemElement.builder()
                                        .type(config.stockSelectionItem.define(type))
                                        .placeholder("item_name", WordUtils.capitalizeFully(type.name().replace('_', ' ')))
                                        .placeholder("price", 50)
                                        .build(ctx);
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
    public @Nullable Element createElement(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem definition) {
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
}
