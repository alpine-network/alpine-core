package co.crystaldev.alpinecore.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Utility for formatting generic strings of text.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@UtilityClass
public final class Formatting {
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
    public String formatPlaceholders(@Nullable String text, @NotNull Object... placeholders) {
        if (text == null)
            return "";
        if (placeholders.length == 0)
            return text;

        if (placeholders.length == 1) {
            // Replace all placeholders with the given value
            text = text.replaceAll("%\\w+%", placeholders[0].toString());
        }
        else {
            for (int i = 0; i < (placeholders.length / 2) * 2; i += 2) {
                String placeholder = "%" + placeholders[i] + "%";
                String value = placeholders[i + 1].toString();
                text = text.replace(placeholder, value);
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
            String value = entry.getValue().toString();
            text = text.replace(placeholder, value);
        }

        return text;
    }
}
