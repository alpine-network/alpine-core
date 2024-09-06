package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.AlpinePluginConfig;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

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
    public static @NotNull String placeholders(@NotNull MiniMessage miniMessage, @Nullable String text,
                                               @NotNull Object... placeholders) {
        if (text == null) {
            return "";
        }
        if (placeholders == null || placeholders.length < 2) {
            return text;
        }

        for (int i = 0; i < (placeholders.length / 2) * 2; i += 2) {
            String placeholder = (String) placeholders[i];
            Object rawReplacer = placeholders[i + 1];
            text = formatPlaceholder(miniMessage, text, rawReplacer, placeholder);
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
    public static @NotNull String placeholders(@NotNull AlpinePlugin plugin, @Nullable String text,
                                               @NotNull Object... placeholders) {
        if (text != null) {
            HashMap<String, String> variables = plugin.getAlpineConfig().variables;
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                text = text.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }

        return placeholders(plugin.getStrictMiniMessage(), text, placeholders);
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
    public static @NotNull String placeholders(@Nullable String text, @NotNull Object... placeholders) {
        return placeholders(MiniMessage.miniMessage(), text, placeholders);
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
    public static @NotNull String placeholders(@NotNull MiniMessage miniMessage, @Nullable String text,
                                               @NotNull Map<String, Object> placeholders) {
        if (text == null) {
            return "";
        }
        if (placeholders.isEmpty()) {
            return text;
        }

        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            String placeholder = "%" + entry.getKey() + "%";
            Object rawReplacer = entry.getValue();
            text = formatPlaceholder(miniMessage, text, rawReplacer, placeholder);
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
    public static @NotNull String placeholders(@NotNull AlpinePlugin plugin, @Nullable String text,
                                               @NotNull Map<String, Object> placeholders) {
        if (text != null) {
            HashMap<String, String> variables = plugin.getAlpineConfig().variables;
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                text = text.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }

        return placeholders(plugin.getStrictMiniMessage(), text, placeholders);
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
    public static @NotNull String placeholders(@Nullable String text, @NotNull Map<String, Object> placeholders) {
        return placeholders(MiniMessage.miniMessage(), text, placeholders);
    }

    /**
     * @deprecated Renamed. Use {@link Formatting#placeholders(MiniMessage, String, Object...)}
     */
    @Deprecated
    public static @NotNull String formatPlaceholders(@NotNull MiniMessage miniMessage, @Nullable String text,
                                                     @NotNull Object... placeholders) {
        return placeholders(miniMessage, text, placeholders);
    }

    /**
     * @see Formatting#placeholders(AlpinePlugin, String, Object...)
     * @deprecated Renamed. Use {@link Formatting#placeholders(AlpinePlugin, String, Object...)}
     */
    @Deprecated
    public static @NotNull String formatPlaceholders(@NotNull AlpinePlugin plugin, @Nullable String text,
                                                     @NotNull Object... placeholders) {
        return placeholders(plugin, text, placeholders);
    }

    /**
     * @see Formatting#placeholders(String, Object...)
     * @deprecated Renamed. Use {@link Formatting#placeholders(String, Object...)}
     */
    @Deprecated
    public static @NotNull String formatPlaceholders(@Nullable String text, @NotNull Object... placeholders) {
        return placeholders(text, placeholders);
    }

    /**
     * @see Formatting#placeholders(MiniMessage, String, Map)
     * @deprecated Renamed. Use {@link Formatting#placeholders(MiniMessage, String, Map)}
     */
    @Deprecated
    public static @NotNull String formatPlaceholders(@NotNull MiniMessage miniMessage, @Nullable String text,
                                                     @NotNull Map<String, Object> placeholders) {
        return placeholders(miniMessage, text, placeholders);
    }

    /**
     * @see Formatting#placeholders(AlpinePlugin, String, Map)
     * @deprecated Renamed. Use {@link Formatting#placeholders(AlpinePlugin, String, Map)}
     */
    @Deprecated
    public static @NotNull String formatPlaceholders(@NotNull AlpinePlugin plugin, @Nullable String text,
                                                     @NotNull Map<String, Object> placeholders) {
        return placeholders(plugin, text, placeholders);
    }

    /**
     * @see Formatting#placeholders(String, Map)
     * @deprecated Renamed. Use {@link Formatting#placeholders(String, Map)}
     */
    @Deprecated
    public static @NotNull String formatPlaceholders(@Nullable String text, @NotNull Map<String, Object> placeholders) {
        return placeholders(text, placeholders);
    }

    private static @NotNull String formatPlaceholder(@NotNull MiniMessage miniMessage, @NotNull String text, Object rawReplacer, String placeholder) {
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
        else if (rawReplacer instanceof Supplier) {
            formattedReplacer = ((Supplier<?>) rawReplacer).get().toString();
        }
        else {
            formattedReplacer = rawReplacer.toString();
        }

        text = text.replace("%" + placeholder + "%", formattedReplacer);
        return text;
    }

    /**
     * Appends padding around the provided component.
     *
     * @param plugin    The owning plugin
     * @param component The component
     * @return The padded component
     */
    public static @NotNull Component applyTitlePadding(@NotNull AlpinePlugin plugin, @NotNull Component component) {
        AlpinePluginConfig config = plugin.getAlpineConfig();
        if (!config.titleUsesPadding) {
            return component;
        }

        int paddingLength = Math.max(4, (config.titlePaddingLength - Components.length(component)) / 2);
        String paddingString = repeat(config.paddingCharacter, paddingLength);
        Component padding = Components.stylize(config.paddingStyle, Component.text(paddingString));
        return Components.join(padding, component, padding);
    }

    /**
     * Formats the given component as a title.
     * <br>
     * This includes applying title formatting and optionally adding padding around the title.
     *
     * @param plugin    The owning plugin
     * @param component The component
     * @return The formatted title component
     */
    public static @NotNull Component title(@NotNull AlpinePlugin plugin, @NotNull Component component) {
        AlpinePluginConfig config = plugin.getAlpineConfig();
        component = config.titleFormat.build(plugin, "content", component);
        return applyTitlePadding(plugin, component);
    }

    /**
     * Converts a collection of elements into a component
     *
     * @param plugin        The owning plugin
     * @param elements      The elements to be displayed
     * @param toComponentFn The function to convert each element into a component
     * @return The displayed components
     */
    public static <T> @NotNull Component elements(@NotNull AlpinePlugin plugin, @NotNull Collection<T> elements,
                                         @NotNull Function<@NotNull T, Component> toComponentFn) {
        AlpinePluginConfig config = plugin.getAlpineConfig();
        List<Component> pageElements = new LinkedList<>();

        // collect page elements
        for (T element : elements) {
            if (element == null) {
                continue;
            }

            pageElements.add(toComponentFn.apply(element));
        }

        // ensure there is data to display
        if (pageElements.isEmpty()) {
            return config.noPages.build(plugin);
        }

        // build the component
        return Components.joinNewLines(pageElements);
    }

    /**
     * Generates a paginated component display for a collection of elements
     *
     * @param plugin          The owning plugin
     * @param title           The title component
     * @param elements        The elements to be paginated
     * @param command         The command used to navigate pages
     * @param currentPage     The current page number (1-indexed)
     * @param elementsPerPage The number of elements to display per page
     * @param toComponentFn   The function to convert each element into a component
     * @return The paginated components
     */
    public static <T> @NotNull Component page(@NotNull AlpinePlugin plugin,
                                     @NotNull Component title, @NotNull Collection<T> elements,
                                     @NotNull String command, int currentPage, int elementsPerPage,
                                     @NotNull Function<@NotNull T, Component> toComponentFn) {
        AlpinePluginConfig config = plugin.getAlpineConfig();
        List<Component> pageElements = new LinkedList<>();
        int totalPages = (int) Math.ceil((double) elements.size() / elementsPerPage);

        // needs to be non-zero based
        int humanPage = currentPage;
        currentPage--;

        // collect page elements
        int index = 0;
        for (T element : elements) {
            if (element == null) {
                continue;
            }

            if (pageElements.size() >= elementsPerPage) {
                break;
            }

            if (index >= currentPage * elementsPerPage) {
                pageElements.add(toComponentFn.apply(element));
            }

            index++;
        }

        // ensure there is data to display
        if (pageElements.isEmpty()) {
            return config.noPages.build(plugin);
        }

        // create the title
        Component previous = currentPage == 0
                ? config.previousDisabled.build(plugin)
                : Components.events(config.previous.build(plugin), Formatting.placeholders(command, "page", humanPage - 1));
        Component next = currentPage == totalPages - 1
                ? config.nextDisabled.build(plugin)
                : Components.events(config.next.build(plugin), Formatting.placeholders(command, "page", humanPage + 1));

        // build the title
        Component center = config.paginatorTitleFormat.build(
                plugin,
                "content", title,
                "page", humanPage,
                "max_pages", totalPages,
                "previous", previous,
                "next", next
        );

        // build the component
        return Components.joinNewLines(
                applyTitlePadding(plugin, center),
                Components.joinNewLines(pageElements)
        );
    }

    /**
     * Generates a progress bar component based on the specified progress value.
     *
     * @param plugin   The owning plugin
     * @param progress The progress value
     * @return The progress bar
     */
    public static @NotNull Component progress(@NotNull AlpinePlugin plugin, double progress) {
        progress = Math.max(0.0, Math.min(1.0, progress));

        AlpinePluginConfig config = plugin.getAlpineConfig();

        int fillLength = (int) (config.progressLength * progress);
        Component progressComponent = Components.join(
                Components.stylize(config.progressIndicatorStyle,
                        Component.text(repeat(config.progressIndicatorCharacter, fillLength))),
                Components.stylize(config.progressRemainingStyle,
                        Component.text(repeat(config.progressRemainingCharacter, config.progressLength - fillLength)))
        );

        return config.progressBarFormat.build(plugin, "progress", progressComponent);
    }

    private static @NotNull String repeat(@NotNull String string, int count) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
