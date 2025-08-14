package co.crystaldev.alpinecore.framework.ui.interaction;

import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public class InteractContext {

    private ActionResult result;

    public InteractContext(@NotNull ActionResult result) {
        this.result = result;
    }

    /**
     * Retrieves the result of the interaction.
     *
     * @return the {@link ActionResult} of the action
     */
    public @NotNull ActionResult result() {
        return this.result;
    }

    /**
     * Sets the result of the interaction.
     *
     * @param result the {@link ActionResult} to set
     */
    public void result(@NotNull ActionResult result) {
        this.result = result;
    }
}
