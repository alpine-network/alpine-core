package co.crystaldev.alpinecore.framework.config.object;

import co.crystaldev.alpinecore.Reference;
import co.crystaldev.alpinecore.util.Formatting;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.Serializer;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple implementation of a configurable plugin message
 * compatible with {@link co.crystaldev.alpinecore.framework.config.AlpineConfig}.
 * <p>
 * Utilizes the Adventure framework for formatting.
 *
 * @see <a href="https://docs.advntr.dev/index.html">Adventure Documentation</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@Configuration @NoArgsConstructor
public class ConfigMessage {
    private String message;

    public ConfigMessage(@NotNull String message) {
        this.message = message;
    }

    public ConfigMessage(@NotNull Component component) {
        this.message = Reference.MINI_MESSAGE.serialize(component);
    }

    /**
     * Formats the text of this message with placeholders, then deserializes
     * it using Adventure's MiniMessage.
     * <p>
     * {@link co.crystaldev.alpinecore.util.Components} should be used to send
     * the result of this method.
     *
     * @see co.crystaldev.alpinecore.util.Components
     * @see net.kyori.adventure.text.minimessage.MiniMessage
     * @param placeholders The placeholders for formatting the message
     * @return The {@link Component}
     */
    @NotNull
    public Component build(@NotNull Object... placeholders) {
        String formatted = Formatting.formatPlaceholders(this.message, placeholders);
        return Reference.MINI_MESSAGE.deserialize(formatted);
    }

    /**
     * Formats the text of this message with placeholders, then deserializes
     * it using Adventure's MiniMessage.
     * <p>
     * {@link co.crystaldev.alpinecore.util.Components} should be used to send
     * the result of this method.
     *
     * @see co.crystaldev.alpinecore.util.Components
     * @see net.kyori.adventure.text.minimessage.MiniMessage
     * @param placeholders The placeholders for formatting the message
     * @return The {@link Component}
     */
    @NotNull
    public Component build(@NotNull Map<String, Object> placeholders) {
        String formatted = Formatting.formatPlaceholders(this.message, placeholders);
        return Reference.MINI_MESSAGE.deserialize(formatted);
    }

    /**
     * Formats the text of this message with placeholders
     *
     * @param placeholders The placeholders for formatting the message
     * @return The string
     */
    @NotNull
    public String buildString(@NotNull Object... placeholders) {
        return Formatting.formatPlaceholders(this.message, placeholders);
    }

    /**
     * Formats the text of this message with placeholders
     *
     * @param placeholders The placeholders for formatting the message
     * @return The string
     */
    @NotNull
    public String buildString(@NotNull Map<String, Object> placeholders) {
        return Formatting.formatPlaceholders(this.message, placeholders);
    }

    public static class Serializer implements de.exlll.configlib.Serializer<ConfigMessage, Object> {
        @Override
        public Object serialize(ConfigMessage element) {
            String[] split = element.message.replace("\r", "").split("\n|<br>");
            return split.length == 1 ? element.message : String.join("\n", split);
        }

        @Override
        public ConfigMessage deserialize(Object element) {
            if (element instanceof Map) {
                return new ConfigMessage((String) ((Map) element).get("message"));
            }
            else if (element instanceof List) {
                return new ConfigMessage(((List<?>) element).stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("\n")));
            }
            else if (element instanceof String) {
                return new ConfigMessage((String) element);
            }
            else {
                return new ConfigMessage(element.toString());
            }
        }
    }
}
