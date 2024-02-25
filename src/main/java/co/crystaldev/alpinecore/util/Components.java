package co.crystaldev.alpinecore.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

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
}
