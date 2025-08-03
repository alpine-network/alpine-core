package co.crystaldev.alpinecore.framework.config;

import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * The primary configuration for your {@link co.crystaldev.alpinecore.AlpinePlugin AlpinePlugin}.
 *
 * @author BestBearr
 * @since 0.4.0
 */
public final class AlpinePluginConfig extends AlpineConfig {

    @ApiStatus.Internal
    public AlpinePluginConfig() {
    }

    @Comment({
            "Specifies an AlpinePlugin configuration to be used in place of this configuration.",
            " ",
            "The plugin name is CaSe SeNsItIvE and must match that of an existing AlpinePlugin.",
            " ",
            "Example usage:",
            " ",
            "overrideWith: AlpineCore"
    })
    public String overrideWith;

    @Comment({
            "",
            "Predefined chat style tags for use in configuration.",
            "These tags can be used to apply specific formatting to chat messages,",
            "making it easier to maintain a consistent style across your application.",
            " ",
            "Example usage:",
            " ",
            "styles:",
            "  info:      dark_aqua bold",
            "  highlight: aqua",
            "  notice:    dark_aqua",
            " ",
            "exampleMessage: <info>Example</info> This is an <highlight>example</highlight>!"
    })
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
            "Predefined variables for use across all your plugin configurations.",
            " ",
            "Example usage:",
            " ",
            "variables:",
            "  prefix: '<bracket>[<info>*</info>]</bracket>'",
            "  error_prefix: '<bracket>[<error>*</error>]</bracket>'"
    })
    public HashMap<String, String> variables = new LinkedHashMap<>();

    @Comment({
            "",
            "Plugin Messages"
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

    @Comment({
            "",
            "Title"
    })
    public ConfigMessage titleFormat = ConfigMessage.of(
            "<bracket><</bracket> %content% <bracket>></bracket>");

    public boolean titleUsesPadding = true;

    public int titlePaddingLength = 54;

    public String paddingCharacter = "-";

    public String paddingStyle = "dark_gray strikethrough";

    @Comment({
            "",
            "Pagination"
    })
    public ConfigMessage paginatorTitleFormat = ConfigMessage.of(
            "<bracket><</bracket> %content% <separator>|</separator> %previous% %page%/%max_pages% %next% <bracket>></bracket>");

    public ConfigMessage previous = ConfigMessage.of(
            "<bracket>[</bracket><emphasis><</emphasis><bracket>]</bracket>");

    public ConfigMessage next = ConfigMessage.of(
            "<bracket>[</bracket><emphasis>></emphasis><bracket>]</bracket>");

    public ConfigMessage previousDisabled = ConfigMessage.of(
            "<emphasis>[<]</emphasis>");

    public ConfigMessage nextDisabled = ConfigMessage.of(
            "<emphasis>[>]</emphasis>");

    public ConfigMessage noPages = ConfigMessage.of(
            "<emphasis><i>No pages available to display</i></emphasis>");

    @Comment({
            "",
            "Progress Indicator"
    })
    public ConfigMessage progressBarFormat = ConfigMessage.of(
            "<bracket>[</bracket>%progress%<bracket>]</bracket>");

    public int progressLength = 20;

    public String progressIndicatorCharacter = "=";

    public String progressRemainingCharacter = "⋯";

    public String progressIndicatorStyle = "aqua strikethrough";

    public String progressRemainingStyle = "light_gray";

    @Override
    public @NotNull String getFileName() {
        return "alpinecore.yml";
    }

    @Configuration
    public static final class InvalidUsageMessages {
        public ConfigMessage single = ConfigMessage.of("<red>Invalid command usage:</red> <gray>%syntax%");
        public ConfigMessage multiHeader = ConfigMessage.of("<red>Invalid command usage:");
        public ConfigMessage multiLine = ConfigMessage.of("<gray><b>  *</b> %syntax%");
    }
}
