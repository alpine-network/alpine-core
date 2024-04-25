package dev.tomwmth.exampleplugin.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
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
}
