package dev.tomwmth.exampleplugin.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;

/**
 * @author Thomas Wearmouth <tomwmth@pm.me>
 * Created on 25/07/2023
 */
public class Config extends AlpineConfig {
    public ConfigMessage prefix = new ConfigMessage("<dark_gray>[</dark_gray><gradient:#e81cff:#40c9ff>Example</gradient><dark_gray>]</dark_gray>");
    public ConfigMessage commandMessage = new ConfigMessage("<gray>%player% has broken <light_purple>%amount%</light_purple> blocks.</gray>");
    public ConfigMessage integrationJoinMessage = new ConfigMessage("<gray>Via reports your protocol version as <light_purple>%protocol%</light_purple>.</gray>");
}
