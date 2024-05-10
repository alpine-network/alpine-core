package dev.tomwmth.exampleplugin.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
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
    public ConfigInventoryUI basicInventory = ConfigInventoryUI.builder()
            .name("<gradient:#a059f7:#2c25f9>Test InventoryUI Screen")
            .slots(
                    "#########",
                    "#|||||||#",
                    "#|||||||#",
                    "#########",
                    "--<-I->--"
            )
            .dictionary(
                    "#", "background",
                    "|", "stock-selection",
                    "-", "footer",
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
            .build();
}
