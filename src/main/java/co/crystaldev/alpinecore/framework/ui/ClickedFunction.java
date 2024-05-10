package co.crystaldev.alpinecore.framework.ui;

/**
 * @since 0.4.0
 */
@FunctionalInterface
public interface ClickedFunction {
    /**
     * Called when a mouse button is clicked.
     *
     * @param button the button that was clicked
     */
    void mouseClicked(int button);
}
