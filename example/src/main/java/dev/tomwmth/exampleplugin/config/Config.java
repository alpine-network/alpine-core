package dev.tomwmth.exampleplugin.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public class Config extends AlpineConfig {
    public ConfigMessage prefix = new ConfigMessage("<dark_gray>[</dark_gray><gradient:#e81cff:#40c9ff>Example</gradient><dark_gray>]</dark_gray>");
    public ConfigMessage commandMessage = new ConfigMessage("<gray>%player% has broken <light_purple>%amount%</light_purple> blocks.</gray>");
    public ConfigMessage actionMessage = new ConfigMessage("<gray>Completed action: <gold>%action%");
    public ConfigMessage integrationJoinMessage = new ConfigMessage("<gray>Via reports your protocol version as <light_purple>%protocol%</light_purple>.</gray>");
}
