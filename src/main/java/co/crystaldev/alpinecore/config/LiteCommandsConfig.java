package co.crystaldev.alpinecore.config;

import co.crystaldev.alpinecore.framework.Initializable;
import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import de.exlll.configlib.Configuration;

/**
 * Configuration for LiteCommands-related messages.
 *
 * @author BestBearr
 * @since 0.2.0
 */
public final class LiteCommandsConfig extends AlpineConfig implements Initializable {

    public ConfigMessage missingPermissions = new ConfigMessage("<red>You don't have the <hover:show_text:'%permission%'>" +
            "required permission</hover> to execute this command");

    public ConfigMessage invalidNumber = new ConfigMessage("<red>%input% is not a number");

    public ConfigMessage playerOnly = new ConfigMessage("<red>This command can only be executed by players");

    public ConfigMessage playerNotFound = new ConfigMessage("<red>Player %player% was not found");

    public ConfigMessage invalidInstant = new ConfigMessage("<red>%input% is not a valid date format. Use <yyyy-MM-dd> " +
            "<HH:mm:ss>");

    public ConfigMessage invalidWorld = new ConfigMessage("<red>%input% is not a valid world");

    public ConfigMessage invalidLocation = new ConfigMessage("<red>%input% is not a valid location");

    public InvalidUsageMessages invalidUsage = new InvalidUsageMessages();

    @Override
    public String getFileName() {
        return "alpinecore.yml";
    }

    @Override
    public boolean init() {
        return false;
    }

    @Configuration
    public static final class InvalidUsageMessages {
        public ConfigMessage single = new ConfigMessage("<red>Invalid command usage.</red> <gray><b>Syntax:</b> %syntax%");
        public ConfigMessage multiHeader = new ConfigMessage("<red>Invalid command usage:");
        public ConfigMessage multiLine = new ConfigMessage("<gray><b>  *</b> %syntax%");
    }
}
