package dev.tomwmth.exampleplugin.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import de.exlll.configlib.Comment;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public class Config extends AlpineConfig {
    public ConfigMessage prefix = ConfigMessage.of("<dark_gray>[</dark_gray><gradient:#e81cff:#40c9ff>Example</gradient><dark_gray>]</dark_gray>");
    public ConfigMessage commandMessage = ConfigMessage.of("<gray>%player% has broken <light_purple>%amount%</light_purple> blocks.</gray>");
    public ConfigMessage actionMessage = ConfigMessage.of("<gray>Completed action: <gold>%action%");

    @Comment({
            "",
            "The custom tags used here are located in the AlpineCore config."
    })
    public ConfigMessage integrationJoinMessage = ConfigMessage.of("<emphasis>Via reports your protocol version as <highlight>%protocol%</highlight>.</emphasis>");
}
