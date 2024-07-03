package dev.tomwmth.exampleplugin.ui;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.element.type.ConfigItemElement;
import co.crystaldev.alpinecore.framework.ui.handler.GenericUIHandler;
import co.crystaldev.alpinecore.framework.ui.type.ConfigInventoryUI;
import co.crystaldev.alpinecore.framework.ui.type.InventoryUI;
import co.crystaldev.alpinecore.util.CollectionUtils;
import com.cryptomorin.xseries.XMaterial;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @since 0.4.0
 */
@RequiredArgsConstructor
public final class StackedUIHandler extends GenericUIHandler {

    public static final List<ConfigInventoryUI> UI_SCREENS = CollectionUtils.linkedList(
            ConfigInventoryUI.builder()
                    .name("<red>Red <gray>(Level 1)")
                    .slots("-X#######")
                    .dictionary("#", "background", "-", "previous", "X", "exit")
                    .item("background", DefinedConfigItem.builder(XMaterial.RED_WOOL)
                            .name("Next Level")
                            .build())
                    .build(),
            ConfigInventoryUI.builder()
                    .name("<gold>Orange <gray>(Level 2)")
                    .slots(
                            "-X#######",
                            "#########"
                    )
                    .dictionary("#", "background", "-", "previous", "X", "exit")
                    .item("background", DefinedConfigItem.builder(XMaterial.ORANGE_WOOL)
                            .name("Next Level")
                            .build())
                    .build(),
            ConfigInventoryUI.builder()
                    .name("<yellow>Yellow <gray>(Level 3)")
                    .slots(
                            "-X#######",
                            "#########",
                            "#########"
                    )
                    .dictionary("#", "background", "-", "previous", "X", "exit")
                    .item("background", DefinedConfigItem.builder(XMaterial.YELLOW_WOOL)
                            .name("Next Level")
                            .build())
                    .build(),
            ConfigInventoryUI.builder()
                    .name("<green>Green <gray>(Level 4)")
                    .slots(
                            "-X#######",
                            "#########",
                            "#########",
                            "#########"
                    )
                    .dictionary("#", "background", "-", "previous", "X", "exit")
                    .item("background", DefinedConfigItem.builder(XMaterial.LIME_WOOL)
                            .name("Next Level")
                            .build())
                    .build(),
            ConfigInventoryUI.builder()
                    .name("<aqua>Blue <gray>(Level 5)")
                    .slots(
                            "-X#######",
                            "#########",
                            "#########",
                            "#########",
                            "#########"
                    )
                    .dictionary("#", "background", "-", "previous", "X", "exit")
                    .item("background", DefinedConfigItem.builder(XMaterial.LIGHT_BLUE_WOOL)
                            .name("Next Level")
                            .build())
                    .build(),
            ConfigInventoryUI.builder()
                    .name("<light_purple>Purple <gray>(Level 6)")
                    .slots(
                            "-X#######",
                            "#########",
                            "#########",
                            "#########",
                            "#########",
                            "#########"
                    )
                    .dictionary("#", "background", "-", "previous", "X", "exit")
                    .item("background", DefinedConfigItem.builder(XMaterial.MAGENTA_WOOL)
                            .name("Next Level")
                            .build())
                    .build()
    );

    private final int index;

    @Override
    public void init(@NotNull UIContext context) {
        context.player().sendMessage("Opened level " + (this.index + 1));
    }

    @Override
    public void closed(@NotNull UIContext context) {
        context.player().sendMessage("Closed level " + (this.index + 1));
    }

    @Override
    public boolean openParentOnClose(@NotNull UIContext context) {
        return true;
    }

    @Override
    public @Nullable Element createElement(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem definition) {
        Element element = null;

        switch (key) {
            case "background":
                element = ConfigItemElement.builder()
                        .type(definition)
                        .build(context);
                element.setOnClick((ctx, click) -> {
                    if (this.index + 1 >= UI_SCREENS.size()) {
                        return;
                    }

                    int index = this.index + 1;
                    InventoryUI ui = UI_SCREENS.get(index).build(ctx.plugin(), new StackedUIHandler(index));
                    ui.view(ctx.player());
                });
                break;
            case "previous":
                element = ConfigItemElement.builder()
                        .type(DefinedConfigItem.builder(XMaterial.BONE)
                                .name("Back to level " + Math.max(1, this.index))
                                .build())
                        .build(context);
                element.setOnClick((ctx, click) -> {
                    if (this.index > 0) {
                        ctx.close(true);
                    }
                });
                break;
            case "exit":
                element = ConfigItemElement.builder()
                        .type(DefinedConfigItem.builder(XMaterial.BARRIER)
                                .name("Close UI")
                                .build())
                        .build(context);
                element.setOnClick((ctx, click) -> {
                    ctx.close(false);
                });
                break;
        }

        return element;
    }
}
