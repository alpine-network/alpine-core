package co.crystaldev.alpinecore.framework.config.object;

import co.crystaldev.alpinecore.Reference;
import co.crystaldev.alpinecore.util.Formatting;
import de.exlll.configlib.Configuration;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

    public ConfigMessage(String message) {
        this.message = message;
    }

    /**
     * Formats the text of this message with placeholders, then deserializes
     * it using Adventure's MiniMessage.
     * <p>
     * {@link co.crystaldev.alpinecore.util.Components} should be used to send
     * the result of this method.
     *
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
}
