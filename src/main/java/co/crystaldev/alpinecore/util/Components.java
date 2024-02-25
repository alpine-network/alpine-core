package co.crystaldev.alpinecore.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @NotNull
    public static Component reset() {
        Style.Builder style = Style.style().color(TextColor.color(-1));
        for (TextDecoration value : TextDecoration.values())
            style.decoration(value, TextDecoration.State.FALSE);
        return Component.text("").color(TextColor.color(-1)).style(style.build());
    }

    /**
     * Joins a variable number of components together
     * with no joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component join(@NotNull Component... components) {
        return Component.join(JoinConfiguration.noSeparators(), components);
    }

    /**
     * Joins a variable number of components together
     * with no joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component join(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.noSeparators(), components);
    }

    /**
     * Joins a variable number of components together
     * with a single space as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinSpaces(@NotNull Component... components) {
        return Component.join(JoinConfiguration.separator(Component.space()), components);
    }

    /**
     * Joins a variable number of components together
     * with a single space as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinSpaces(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.separator(Component.space()), components);
    }

    /**
     * Joins a variable number of components together
     * with a single comma as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinCommas(@NotNull Component... components) {
        return Component.join(JoinConfiguration.commas(true), components);
    }

    /**
     * Joins a variable number of components together
     * with a single comma as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinCommas(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.commas(true), components);
    }

    /**
     * Joins a variable number of components together
     * with a single new line as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinNewLines(@NotNull Component... components) {
        return Component.join(JoinConfiguration.newlines(), components);
    }

    /**
     * Joins a variable number of components together
     * with a single new line as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinNewLines(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.newlines(), components);
    }

    /**
     * Apply the given style to the given component.
     *
     * @param style     The style.
     * @param component The component.
     * @return The stylized component.
     */
    @NotNull
    public static Component stylize(@Nullable String style, @NotNull Component component, boolean force) {
        if (style == null) {
            return component;
        }

        if (!force) {
            return stylize(style, component);
        }

        for (StyleBuilderApplicable type : parseStyle(style)) {
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
    @NotNull
    public static Component stylize(@Nullable String style, @NotNull Component component) {
        if (style == null) {
            return component;
        }

        TextComponent.Builder builder = Component.text();
        for (StyleBuilderApplicable type : parseStyle(style)) {
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

    @NotNull @ApiStatus.Internal
    public static List<StyleBuilderApplicable> parseStyle(@NotNull String style) {
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
}
