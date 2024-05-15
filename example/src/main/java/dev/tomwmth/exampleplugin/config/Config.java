package dev.tomwmth.exampleplugin.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.config.object.item.VaryingConfigItem;
import co.crystaldev.alpinecore.framework.ui.type.ConfigInventoryUI;
import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Comment;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public class Config extends AlpineConfig {
    public ConfigMessage commandMessage = ConfigMessage.of("%prefix% <gray>%player% has broken <light_purple>%amount%</light_purple> blocks.</gray>");
    public ConfigMessage actionMessage = ConfigMessage.of("%prefix% <gray>Completed action: <gold>%action%");

    @Comment({
            "",
            "The custom tags used here are located in the AlpineCore config."
    })
    public ConfigMessage integrationJoinMessage = ConfigMessage.of("%prefix% <emphasis>Via reports your protocol version as <highlight>%protocol%</highlight>.</emphasis>");

    @Comment({
            "",
            "Create an advanced and configurable inventory",
            "UI with ease!"
    })
    public ConfigInventoryUI paginatedUI = ConfigInventoryUI.builder()
            .name("<gradient:#a074f2:#f27a74><b>Test InventoryUI Screen")
            .slots(
                    "#########",
                    "#|||||||#",
                    "#|||||||#",
                    "#########",
                    "--<-I->--"
            )
            .dictionary(
                    "#", "background",
                    "-", "footer",
                    "|", "stock-selection",
                    "<", "previous-page",
                    ">", "next-page",
                    "I", "page-info"
            )
            .item("background", DefinedConfigItem
                    .builder(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .name("")
                    .build())
            .item("footer", DefinedConfigItem
                    .builder(XMaterial.BLACK_STAINED_GLASS_PANE)
                    .name("")
                    .build())
            .item("previous-page", DefinedConfigItem
                    .builder(XMaterial.ARROW)
                    .name("<info>Previous Page</info> <bracket>[<emphasis>Left Click</emphasis>]</bracket>")
                    .build())
            .item("next-page", DefinedConfigItem
                    .builder(XMaterial.ARROW)
                    .name("<info>Next Page</info> <bracket>[<emphasis>Left Click</emphasis>]</bracket>")
                    .build())
            .item("page-info", DefinedConfigItem
                    .builder(XMaterial.CLOCK)
                    .name("<info>Page Information</info>")
                    .lore(
                            "<info>  *</info> <emphasis>Current Page:</emphasis> %page%/%page_count%",
                            "<info>  *</info> <emphasis>Total Elements:</emphasis> %element_count%"
                    )
                    .build())
            .build();

    @Comment("")
    public VaryingConfigItem stockSelectionItem = VaryingConfigItem.builder()
            .name("<highlight>%item_name%</highlight>")
            .lore(
                    "<info>  *</info> <emphasis>Price:</emphasis> $%price%/ea",
                    "<info>  *</info> <emphasis>Purchase:</emphasis> Left Click",
                    "<info>  *</info> <emphasis>Sell All:</emphasis> Right Click"
            )
            .build();

    @Comment("")
    public DefinedConfigItem stockPlaceholderItem = DefinedConfigItem.builder(XMaterial.WHITE_STAINED_GLASS_PANE)
            .name("")
            .build();

    @Comment({
            "",
            "This is another basic UI screen meant to demo",
            "user interaction with elements"
    })
    public ConfigInventoryUI mutableUI = ConfigInventoryUI.builder()
            .name("<gradient:#a074f2:#f27a74><b>Test InventoryUI Screen")
            .slots(
                    "#########",
                    "#|||||||#",
                    "#|||||||#",
                    "#|||||||#",
                    "#|||||||#",
                    "#########"
            )
            .dictionary(
                    "#", "background",
                    "|", "storage"
            )
            .item("background", DefinedConfigItem
                    .builder(XMaterial.BLACK_STAINED_GLASS_PANE)
                    .name("")
                    .enchanted()
                    .build())
            .build();
}
