package co.crystaldev.alpinecore.config;

import co.crystaldev.alpinecore.framework.Initializable;
import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Configuration for LiteCommands-related messages.
 *
 * @author BestBearr
 * @since 0.2.0
 */
public final class AlpineCoreConfig extends AlpineConfig implements Initializable {

    @Comment("Predefined chat style tags used in config.")
    public HashMap<String, String> styles = new LinkedHashMap<>();
    {
        this.styles.put("info", "dark_aqua bold");
        this.styles.put("highlight", "aqua");
        this.styles.put("notice", "dark_aqua");

        this.styles.put("error", "red bold");
        this.styles.put("error_highlight", "red");

        this.styles.put("success", "green bold");
        this.styles.put("success_highlight", "green");

        this.styles.put("emphasis", "gray");
        this.styles.put("bracket", "dark_gray");
        this.styles.put("separator", "dark_gray bold");
        this.styles.put("text", "white");
        this.styles.put("error_text", "white");
    }

    @Comment({
            "",
            "Command messages."
    })
    public ConfigMessage missingPermissions = ConfigMessage.of("<red>You don't have the <hover:show_text:'%permission%'>" +
            "required permission</hover> to execute this command");

    public ConfigMessage invalidNumber = ConfigMessage.of("<red>%input% is not a number");

    public ConfigMessage playerOnly = ConfigMessage.of("<red>This command can only be executed by players");

    public ConfigMessage playerNotFound = ConfigMessage.of("<red>Player %player% was not found");

    public ConfigMessage invalidInstant = ConfigMessage.of("<red>%input% is not a valid date format. Use <yyyy-MM-dd> " +
            "<HH:mm:ss>");

    public ConfigMessage invalidWorld = ConfigMessage.of("<red>%input% is not a valid world");

    public ConfigMessage invalidLocation = ConfigMessage.of("<red>%input% is not a valid location");

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
        public ConfigMessage single = ConfigMessage.of("<red>Invalid command usage.</red> <gray>%syntax%");
        public ConfigMessage multiHeader = ConfigMessage.of("<red>Invalid command usage:");
        public ConfigMessage multiLine = ConfigMessage.of("<gray><b>  *</b> %syntax%");
    }
}
