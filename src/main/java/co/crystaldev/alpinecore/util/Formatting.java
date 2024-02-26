package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.config.AlpineCoreConfig;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for formatting generic strings of text.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@UtilityClass
public final class Formatting {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    /**
     * Formats text with placeholders.
     * <p>
     * Placeholders are denoted with percent symbols on either side.
     *
     * @param miniMessage  The {@link MiniMessage} instance.
     * @param text         The text to format
     * @param placeholders The placeholders used to format the text
     * @return The formatted text
     */
    @NotNull
    public static String formatPlaceholders(@NotNull MiniMessage miniMessage, @Nullable String text, @NotNull Object... placeholders) {
        if (text == null)
            return "";
        if (placeholders.length == 0)
            return text;

        if (placeholders.length == 1) {
            // Replace all placeholders with given value
            text = text.replaceAll("%\\w+%", placeholders[0].toString());
        }
        else {
            for (int i = 0; i < (placeholders.length / 2) * 2; i += 2) {
                String placeholder = (String) placeholders[i];
                Object rawReplacer = placeholders[i + 1];
                String formattedReplacer;

                if (rawReplacer instanceof Float || rawReplacer instanceof Double) {
                    formattedReplacer = DECIMAL_FORMAT.format(rawReplacer);
                }
                else if (rawReplacer instanceof Boolean) {
                    formattedReplacer = (Boolean) rawReplacer ? "True" : "False";
                }
                else if (rawReplacer instanceof Component) {
                    formattedReplacer = miniMessage.serialize(((Component) rawReplacer).append(Components.reset()));
                }
                else {
                    formattedReplacer = rawReplacer.toString();
                }

                text = text.replaceAll("%" + placeholder + "%", formattedReplacer);
            }
        }

        return text;
    }

    /**
     * Formats text with placeholders.
     * <p>
     * Placeholders are denoted with percent symbols on either side.
     *
     * @param plugin       The owning plugin.
     * @param text         The text to format
     * @param placeholders The placeholders used to format the text
     * @return The formatted text
     */
    @NotNull
    public static String formatPlaceholders(@NotNull AlpinePlugin plugin, @Nullable String text, @NotNull Object... placeholders) {
        if (text != null) {
            HashMap<String, String> variables = plugin.getConfiguration(AlpineCoreConfig.class).variables;
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                text = text.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }

        return formatPlaceholders(plugin.getStrictMiniMessage(), text, placeholders);
    }

    /**
     * Formats text with placeholders.
     * <p>
     * Placeholders are denoted with percent symbols on either side.
     *
     * @param text         The text to format
     * @param placeholders The placeholders used to format the text
     * @return The formatted text
     */
    @NotNull
    public static String formatPlaceholders(@Nullable String text, @NotNull Object... placeholders) {
        return formatPlaceholders(MiniMessage.miniMessage(), text, placeholders);
    }

    /**
     * Formats text with placeholders.
     * <p>
     * Placeholders are denoted with percent symbols on either side.
     *
     * @param miniMessage  The {@link MiniMessage} instance.
     * @param text         The text to format
     * @param placeholders The placeholders used to format the text
     * @return The formatted text
     */
    @NotNull
    public static String formatPlaceholders(@NotNull MiniMessage miniMessage, @Nullable String text, @NotNull Map<String, Object> placeholders) {
        if (text == null)
            return "";
        if (placeholders.size() == 0)
            return text;

        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            String placeholder = "%" + entry.getKey() + "%";
            Object rawReplacer = entry.getValue();
            String formattedReplacer;

            if (rawReplacer instanceof Float || rawReplacer instanceof Double) {
                formattedReplacer = DECIMAL_FORMAT.format(rawReplacer);
            }
            else if (rawReplacer instanceof Boolean) {
                formattedReplacer = (Boolean) rawReplacer ? "True" : "False";
            }
            else if (rawReplacer instanceof Component) {
                formattedReplacer = miniMessage.serialize(((Component) rawReplacer).append(Components.reset()));
            }
            else {
                formattedReplacer = rawReplacer.toString();
            }

            text = text.replace(placeholder, formattedReplacer);
        }

        return text;
    }

    /**
     * Formats text with placeholders.
     * <p>
     * Placeholders are denoted with percent symbols on either side.
     *
     * @param plugin       The owning plugin.
     * @param text         The text to format
     * @param placeholders The placeholders used to format the text
     * @return The formatted text
     */
    @NotNull
    public static String formatPlaceholders(@NotNull AlpinePlugin plugin, @Nullable String text, @NotNull Map<String, Object> placeholders) {
        if (text != null) {
            HashMap<String, String> variables = plugin.getConfiguration(AlpineCoreConfig.class).variables;
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                text = text.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }

        return formatPlaceholders(plugin.getStrictMiniMessage(), text, placeholders);
    }

    /**
     * Formats text with placeholders.
     * <p>
     * Placeholders are denoted with percent symbols on either side.
     *
     * @param text         The text to format
     * @param placeholders The placeholders used to format the text
     * @return The formatted text
     */
    @NotNull
    public static String formatPlaceholders(@Nullable String text, @NotNull Map<String, Object> placeholders) {
        return formatPlaceholders(MiniMessage.miniMessage(), text, placeholders);
    }
}
