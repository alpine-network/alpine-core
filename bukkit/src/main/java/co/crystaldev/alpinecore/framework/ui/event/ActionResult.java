package co.crystaldev.alpinecore.framework.ui.event;

/**
 * @since 0.4.0
 */
public enum ActionResult {
    /**|
     * Continues on with the action.
     */
    SUCCESS,

    /**
     * Fallback to the next event listener.
     */
    PASS,

    /**
     * Cancel the action outright.
     */
    CANCEL
}
