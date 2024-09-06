package co.crystaldev.alpinecore.framework.config.object;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.integration.PlaceholderIntegration;
import co.crystaldev.alpinecore.util.Formatting;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.Serializer;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A simple implementation of a configurable plugin message
 * compatible with {@link co.crystaldev.alpinecore.framework.config.AlpineConfig}.
 * <p>
 * Utilizes the Adventure framework for formatting.
 * </p>
 * Example usage:
 * <pre>{@code
 * AlpinePlugin plugin = ; // your plugin instance
 * ConfigMessage message = ConfigMessage.of(
 *      "<info>PluginName:</info> This is the first part of the message",
 *      "<notice> -</notice> This is the next line (%placeholder%)"
 * );
 * Component component = message.build(plugin, "placeholder", 20);
 * Messaging.send(player, component);
 * }</pre>
 *
 * @see <a href="https://docs.advntr.dev/index.html">Adventure Documentation</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@NoArgsConstructor
@Configuration
public class ConfigMessage {

    protected List<String> message;

    protected ConfigMessage(@NotNull List<String> message) {
        List<String> processedMessage = new LinkedList<>();
        for (String s : message) {
            processedMessage.addAll(Arrays.asList(s.split("(<br>|\n|\r)")));
        }
        this.message = processedMessage;
    }

    protected ConfigMessage(@NotNull String message) {
        this(Arrays.asList(message.split("(<br>|\n|\r)")));
    }

    public static @NotNull ConfigMessage of(@NotNull String component) {
        return new ConfigMessage(Collections.singletonList(component));
    }

    public static @NotNull ConfigMessage of(@NotNull String... components) {
        return new ConfigMessage(Arrays.asList(components));
    }

    /**
     * Formats the text of this message with placeholders, then deserializes
     * it using Adventure's MiniMessage.
     * <p>
     * {@link co.crystaldev.alpinecore.util.Messaging} should be used to send
     * the result of this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param placeholders The placeholders for formatting the message
     * @return The {@link Component}
     *
     * @see co.crystaldev.alpinecore.util.Components
     * @see co.crystaldev.alpinecore.util.Messaging
     * @see net.kyori.adventure.text.minimessage.MiniMessage
     */
    public @NotNull Component build(@NotNull AlpinePlugin plugin, @NotNull Object... placeholders) {
        String formatted = Formatting.placeholders(plugin, String.join("\n", this.message), placeholders);
        return plugin.getMiniMessage().deserialize(formatted);
    }

    /**
     * Formats the text of this message with placeholders, then deserializes
     * it using Adventure's MiniMessage.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     * <p>
     * {@link co.crystaldev.alpinecore.util.Messaging} should be used to send
     * the result of this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param target       The target player.
     * @param placeholders The placeholders for formatting the message
     * @return The {@link Component}
     *
     * @see co.crystaldev.alpinecore.integration.PlaceholderIntegration
     * @see co.crystaldev.alpinecore.util.Components
     * @see co.crystaldev.alpinecore.util.Messaging
     * @see net.kyori.adventure.text.minimessage.MiniMessage
     */
    public @NotNull Component build(@NotNull AlpinePlugin plugin, @NotNull OfflinePlayer target,
                                    @NotNull Object... placeholders) {
        String formatted = PlaceholderIntegration.getInstance().replace(target,
                Formatting.placeholders(plugin, String.join("\n", this.message), placeholders));
        return plugin.getMiniMessage().deserialize(formatted);
    }

    /**
     * Formats the text of this message with placeholders, then deserializes
     * it using Adventure's MiniMessage.
     * <p>
     * {@link co.crystaldev.alpinecore.util.Messaging} should be used to send
     * the result of this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param placeholders The placeholders for formatting the message
     * @return The {@link Component}
     *
     * @see co.crystaldev.alpinecore.util.Components
     * @see co.crystaldev.alpinecore.util.Messaging
     * @see net.kyori.adventure.text.minimessage.MiniMessage
     */
    public @NotNull Component build(@NotNull AlpinePlugin plugin, @NotNull Map<String, Object> placeholders) {
        String formatted = Formatting.placeholders(plugin, String.join("\n", this.message), placeholders);
        return plugin.getMiniMessage().deserialize(formatted);
    }

    /**
     * Formats the text of this message with placeholders, then deserializes
     * it using Adventure's MiniMessage.
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     * <p>
     * {@link co.crystaldev.alpinecore.util.Messaging} should be used to send
     * the result of this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param target       The target player.
     * @param other        The relational player.
     * @param placeholders The placeholders for formatting the message
     * @return The {@link Component}
     *
     * @see co.crystaldev.alpinecore.integration.PlaceholderIntegration
     * @see co.crystaldev.alpinecore.util.Components
     * @see co.crystaldev.alpinecore.util.Messaging
     * @see net.kyori.adventure.text.minimessage.MiniMessage
     */
    public @NotNull Component build(@NotNull AlpinePlugin plugin, @NotNull OfflinePlayer target,
                                    @NotNull OfflinePlayer other, @NotNull Object... placeholders) {
        String formatted = PlaceholderIntegration.getInstance().replace(target, other,
                Formatting.placeholders(plugin, String.join("\n", this.message), placeholders));
        return plugin.getMiniMessage().deserialize(formatted);
    }

    /**
     * Formats the text of this message with placeholders
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param placeholders The placeholders for formatting the message
     * @return The string
     */
    public @NotNull String buildString(@NotNull AlpinePlugin plugin, @NotNull Object... placeholders) {
        return Formatting.placeholders(plugin, String.join("\n", this.message), placeholders);
    }

    /**
     * Formats the text of this message with placeholders
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param target       The target player.
     * @param placeholders The placeholders for formatting the message
     * @return The string
     *
     * @see co.crystaldev.alpinecore.integration.PlaceholderIntegration
     */
    public @NotNull String buildString(@NotNull AlpinePlugin plugin, @NotNull OfflinePlayer target,
                                       @NotNull Object... placeholders) {
        return PlaceholderIntegration.getInstance().replace(target,
                Formatting.placeholders(plugin, String.join("\n", this.message), placeholders));
    }

    /**
     * Formats the text of this message with placeholders
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param placeholders The placeholders for formatting the message
     * @return The string
     */
    public @NotNull String buildString(@NotNull AlpinePlugin plugin, @NotNull Map<String, Object> placeholders) {
        return Formatting.placeholders(plugin, String.join("\n", this.message), placeholders);
    }

    /**
     * Formats the text of this message with placeholders
     * <p>
     * PlaceholderAPI placeholders are replaced by this method.
     *
     * @param plugin       The main plugin instance used for contextual operations
     * @param target       The target player.
     * @param other        The relational player.
     * @param placeholders The placeholders for formatting the message
     * @return The string
     *
     * @see co.crystaldev.alpinecore.integration.PlaceholderIntegration
     */
    public @NotNull String buildString(@NotNull AlpinePlugin plugin, @NotNull OfflinePlayer target,
                                       @NotNull OfflinePlayer other, @NotNull Object... placeholders) {
        return PlaceholderIntegration.getInstance().replace(target, other,
                Formatting.placeholders(plugin, String.join("\n", this.message), placeholders));
    }

    public static final class Adapter implements Serializer<ConfigMessage, Object> {
        @Override
        public Object serialize(ConfigMessage element) {
            return String.join("\n", element.message);
        }

        @Override
        public ConfigMessage deserialize(Object element) {
            if (element instanceof Map) {
                Object value = ((Map) element).get("message");
                List<String> message = value instanceof Collection<?>
                        ? new LinkedList<>((Collection<String>) value)
                        : Collections.singletonList(value.toString());
                return new ConfigMessage(message);
            }
            else if (element instanceof List) {
                return new ConfigMessage(((List<?>) element).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()));
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
