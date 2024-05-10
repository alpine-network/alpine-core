package dev.tomwmth.exampleplugin.ui;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.element.ElementPaginator;
import co.crystaldev.alpinecore.framework.ui.element.ElementProvider;
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

    private static final MappedMaterial BOATS_AND_DOORS_ROCK = MappedMaterial.builder()
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
                            .entries(BOATS_AND_DOORS_ROCK.getMaterials())
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
    public @Nullable Element createEntry(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem dictionaryDefinition) {
        switch (key) {
            case "stock-selection":
                return this.paginator.nextElement(context);
            case "previous-page":
                if (dictionaryDefinition == null) {
                    return null;
                }
                return this.paginator.buildPrevious(context, dictionaryDefinition);
            case "next-page":
                if (dictionaryDefinition == null) {
                    return null;
                }
                return this.paginator.buildNext(context, dictionaryDefinition);
            case "page-info":
                if (dictionaryDefinition == null) {
                    return null;
                }
                return this.paginator.buildInfo(context, dictionaryDefinition);
            default:
                return null;
        }
    }
}
