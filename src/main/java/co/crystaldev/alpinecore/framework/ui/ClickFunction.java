package co.crystaldev.alpinecore.framework.ui;

import co.crystaldev.alpinecore.framework.ui.element.ClickContext;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@FunctionalInterface
public interface ClickFunction {
    /**
     * Called when a mouse button is clicked.
     *
     * @param context the ui context for the interaction
     * @param click   the click context for the interaction
     */
    void mouseClicked(@NotNull UIContext context, @NotNull ClickContext click);
}
