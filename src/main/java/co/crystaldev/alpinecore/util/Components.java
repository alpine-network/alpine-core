package co.crystaldev.alpinecore.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.*;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility for interacting with Adventure {@link Component}
 * objects.
 *
 * @see <a href="https://docs.advntr.dev/text.html">Text (Chat Components)</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@UtilityClass @SuppressWarnings("unused")
public final class Components {

    /** Map containing vanilla chat formatting codes */
    private static final Map<StyleBuilderApplicable, List<String>> STYLE_TO_ALIAS_MAP = ImmutableMap.<StyleBuilderApplicable, List<String>>builder()
            .put(TextDecoration.BOLD, ImmutableList.of("bold", "&l", "l"))
            .put(TextDecoration.ITALIC, ImmutableList.of("italic", "&o", "o"))
            .put(TextDecoration.OBFUSCATED, ImmutableList.of("obfuscated", "&k", "k", "magic"))
            .put(TextDecoration.UNDERLINED, ImmutableList.of("underlined", "&n", "n", "underline", "uline"))
            .put(TextDecoration.STRIKETHROUGH, ImmutableList.of("strikethrough", "&m", "m", "strike"))
            .put(NamedTextColor.BLACK, ImmutableList.of("black", "&0", "0"))
            .put(NamedTextColor.DARK_BLUE, ImmutableList.of("dark_blue", "&1", "1"))
            .put(NamedTextColor.DARK_GREEN, ImmutableList.of("dark_green", "&2", "2"))
            .put(NamedTextColor.DARK_AQUA, ImmutableList.of("dark_aqua", "&3", "3"))
            .put(NamedTextColor.DARK_RED, ImmutableList.of("dark_red", "&4", "4"))
            .put(NamedTextColor.DARK_PURPLE, ImmutableList.of("dark_purple", "&5", "5", "dark_magenta"))
            .put(NamedTextColor.GOLD, ImmutableList.of("gold", "&6", "6"))
            .put(NamedTextColor.GRAY, ImmutableList.of("gray", "grey", "&7", "7"))
            .put(NamedTextColor.DARK_GRAY, ImmutableList.of("dark_gray", "dark_grey", "&8", "8"))
            .put(NamedTextColor.BLUE, ImmutableList.of("blue", "&9", "9"))
            .put(NamedTextColor.GREEN, ImmutableList.of("green", "&a", "a"))
            .put(NamedTextColor.AQUA, ImmutableList.of("aqua", "&b", "b"))
            .put(NamedTextColor.RED, ImmutableList.of("red", "&c", "c"))
            .put(NamedTextColor.LIGHT_PURPLE, ImmutableList.of("light_purple", "&d", "d", "magenta", "purple"))
            .put(NamedTextColor.YELLOW, ImmutableList.of("yellow", "&e", "e"))
            .put(NamedTextColor.WHITE, ImmutableList.of("white", "&f", "f"))
            .build();

    /**
     * Constructs a component that can be used to reset
     * all existing styling.
     *
     * @return The reset component
     */
    public static @NotNull Component reset() {
        Style.Builder style = Style.style().color(NamedTextColor.WHITE);
        for (TextDecoration value : TextDecoration.values()) {
            style.decoration(value, TextDecoration.State.FALSE);
        }
        return Component.text("").color(NamedTextColor.WHITE).style(style.build());
    }

    /**
     * Get the length of the given component.
     *
     * @param component The component.
     * @return The length.
     */
    public static int length(@Nullable Component component) {
        if (component == null) {
            return 0;
        }

        return PlainTextComponentSerializer.plainText().serialize(component).length();
    }

    // region Join

    /**
     * Joins a variable number of components together
     * with no joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    public static @NotNull Component join(@NotNull Component... components) {
        return Component.join(JoinConfiguration.noSeparators(), components);
    }

    /**
     * Joins a variable number of components together
     * with no joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    public static @NotNull Component join(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.noSeparators(), components);
    }

    /**
     * Joins a variable number of components together
     * with a single space as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    public static @NotNull Component joinSpaces(@NotNull Component... components) {
        return Component.join(JoinConfiguration.separator(Component.space()), components);
    }

    /**
     * Joins a variable number of components together
     * with a single space as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    public static @NotNull Component joinSpaces(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.separator(Component.space()), components);
    }

    /**
     * Joins a variable number of components together
     * with a single comma as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    public static @NotNull Component joinCommas(@NotNull Component... components) {
        return Component.join(JoinConfiguration.commas(true), components);
    }

    /**
     * Joins a variable number of components together
     * with a single comma as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    public static @NotNull Component joinCommas(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.commas(true), components);
    }

    /**
     * Joins a variable number of components together
     * with a single new line as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    public static @NotNull Component joinNewLines(@NotNull Component... components) {
        return Component.join(JoinConfiguration.newlines(), components);
    }

    /**
     * Joins a variable number of components together
     * with a single new line as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    public static @NotNull Component joinNewLines(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.newlines(), components);
    }

    // endregion Join

    // region Events

    /**
     * Add events to the given component.
     *
     * @param component The component to add hover and click events to.
     * @param hover   The text to display while hovering.
     * @param command The command to execute when the component is clicked.
     * @return The component.
     */
    public static @NotNull Component events(@NotNull Component component, @NotNull Component hover, @NotNull String command) {
        return component.hoverEvent(HoverEvent.showText(hover)).clickEvent(ClickEvent.runCommand(command));
    }

    /**
     * Add events to the given component.
     *
     * @param component The component to add hover and click events to.
     * @param both The text to display while hovering and command to execute when clicked.
     * @return The component.
     */
    public static @NotNull Component events(@NotNull Component component, @NotNull Component both) {
        return events(component, both, PlainTextComponentSerializer.plainText().serialize(both));
    }

    /**
     * Add events to the given component.
     *
     * @param component The component to add hover and click events to.
     * @param both The text to display while hovering and command to execute when clicked.
     * @return The component.
     */
    public static @NotNull Component events(@NotNull Component component, @NotNull String both) {
        return events(component, Component.text(both), both);
    }

    // endregion Events

    // region Styling

    /**
     * Apply the given style to the given component.
     *
     * @param style     The style.
     * @param component The component.
     * @return The stylized component.
     */
    public static @NotNull Component stylize(@Nullable String style, @NotNull Component component, boolean force) {
        if (style == null) {
            return component;
        }

        if (!force) {
            return stylize(style, component);
        }

        for (StyleBuilderApplicable type : processStyle(style)) {
            if (type instanceof TextColor) {
                component = component.color((TextColor) type);
            }
            else {
                component = component.decorate((TextDecoration) type);
            }
        }
        return component;
    }

    /**
     * Apply the given style to the given component.
     *
     * @param style     The style.
     * @param component The component.
     * @return The stylized component.
     */
    public static @NotNull Component stylize(@Nullable String style, @NotNull Component component) {
        if (style == null) {
            return component;
        }

        TextComponent.Builder builder = Component.text();
        for (StyleBuilderApplicable type : processStyle(style)) {
            if (type instanceof TextColor) {
                builder.color((TextColor) type);
            }
            else {
                builder.decorate((TextDecoration) type);
            }
        }
        builder.append(component);
        return builder.asComponent();
    }

    @ApiStatus.Internal
    public static @NotNull List<StyleBuilderApplicable> processStyle(@NotNull String style) {
        List<StyleBuilderApplicable> styles = new ArrayList<>();
        for (String component : style.split(" +")) {
            StyleBuilderApplicable parsedComponent = null;

            // Match any known style or color
            for (Map.Entry<StyleBuilderApplicable, List<String>> entry : STYLE_TO_ALIAS_MAP.entrySet()) {
                List<String> aliases = entry.getValue();
                if (aliases.contains(component.toLowerCase())) {
                    parsedComponent = entry.getKey();
                    break;
                }
            }

            // Attempt to parse a hex string
            if (parsedComponent == null) {
                if (!component.startsWith(TextColor.HEX_PREFIX)) {
                    component = TextColor.HEX_PREFIX + component;
                }
                parsedComponent = TextColor.fromHexString(component);
            }

            if (parsedComponent != null) {
                styles.add(parsedComponent);
            }
        }

        return styles;
    }

    // endregion Styling

    // region Join

    /**
     * Splits a component and the underlying component tree by a regex.
     * Styles are being preserved, so splitting {@code <green>line1\nline2</green>} by the regex "\n" will produce
     * two {@link Component} with {@link TextColor} green.
     * It matches the regex for all components and their content but not for a pattern that goes beyond one component.
     * {@code line1<green>ab</green>cline2} can therefore not be split with the regex "abc", because only "ab" or "c" would
     * match.
     *
     * @author <a href=https://github.com/CubBossa>CubBossa</a>
     * @see <a href=https://gist.github.com/CubBossa/8bc84706b7e1e393bebb7dc9cf8a9ed5>Split components by their content and a regex</a>
     *
     * @param self A component to split at a regex.
     * @param separator A regex to split the TextComponent content at.
     * @return A list of new components
     */
    public static @NotNull List<Component> split(@NotNull Component self, @NotNull @RegExp String separator) {
        // First split component content
        List<Component> lines = splitComponentContent(self, separator);

        if (self.children().isEmpty()) {
            return lines;
        }

        // Extract last split, which will contain all children of the same line
        Component parent = lines.remove(lines.size() - 1);

        // Process each child in order
        for (Component child : self.children()) {
            // Split child to List<Component>
            List<? extends Component> childSegments = split(child, separator);

            // each split will be a new row, except the first which will stick to the parent
            parent = parent.append(childSegments.get(0));
            for (int i = 1; i < childSegments.size(); i++) {
                lines.add(parent);
                parent = Component.empty().style(parent.style());
                parent = parent.append(childSegments.get(i));
            }
        }
        lines.add(parent);

        return lines;
    }

    private static @NotNull List<Component> splitComponentContent(@NotNull Component component, @RegExp String regex) {
        if (!(component instanceof TextComponent)) {
            return Collections.singletonList(component);
        }

        TextComponent textComponent = (TextComponent) component;
        String[] segments = textComponent.content().split(regex);
        if (segments.length == 0) {
            // Special case if the split regex is equals to the content.
            segments = new String[]{"", ""};
        }
        return Arrays.stream(segments)
                .map(s -> Component.text(s).style(textComponent.style()))
                .map(c -> (Component) c)
                .collect(Collectors.toList());
    }

    // endregion Join
}
