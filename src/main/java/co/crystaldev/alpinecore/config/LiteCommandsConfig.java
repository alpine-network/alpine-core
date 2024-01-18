package co.crystaldev.alpinecore.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;

/**
 * Configuration for LiteCommands-related messages.
 *
 * @author BestBearr
 * @since 0.2.0
 */
public final class LiteCommandsConfig extends AlpineConfig {

    public ConfigMessage missingPermissions = new ConfigMessage("<red>You don't have the <hover:show_text:'%permission%'>" +
            "required permission</hover> to execute this command");

    public ConfigMessage invalidNumber = new ConfigMessage("<red>%input% is not a number");

    public ConfigMessage invalidUsage = new ConfigMessage("<red>Invalid command usage provided");

    public ConfigMessage playerOnly = new ConfigMessage("<red>This command can only be executed by players");

    public ConfigMessage playerNotFound = new ConfigMessage("<red>Player %player% was not found");

    public ConfigMessage invalidInstant = new ConfigMessage("<red>%input% is not a valid date format. Use <yyyy-MM-dd> " +
            "<HH:mm:ss>");

    public ConfigMessage invalidWorld = new ConfigMessage("<red>%input% is not a valid world");

    public ConfigMessage invalidLocation = new ConfigMessage("<red>%input% is not a valid location");

    @Override
    public String getFileName() {
        return "litecommands.yml";
    }
}
