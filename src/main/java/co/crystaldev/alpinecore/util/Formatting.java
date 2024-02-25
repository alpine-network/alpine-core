package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.Reference;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
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
     * @param text         The text to format
     * @param placeholders The placeholders used to format the text
     * @return The formatted text
     */
    @NotNull
    public static String formatPlaceholders(@Nullable String text, @NotNull Object... placeholders) {
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
                    formattedReplacer = Reference.STRICT_MINI_MESSAGE.serialize(((Component) rawReplacer).append(Components.reset()));
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
     * @param text         The text to format
     * @param placeholders The placeholders used to format the text
     * @return The formatted text
     */
    @NotNull
    public String formatPlaceholders(@Nullable String text, @NotNull Map<String, Object> placeholders) {
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
                formattedReplacer = Reference.STRICT_MINI_MESSAGE.serialize(((Component) rawReplacer).append(Components.reset()));
            }
            else {
                formattedReplacer = rawReplacer.toString();
            }

            text = text.replace(placeholder, formattedReplacer);
        }

        return text;
    }
}
