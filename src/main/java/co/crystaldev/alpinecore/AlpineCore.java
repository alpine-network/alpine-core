package co.crystaldev.alpinecore;

import lombok.Getter;

/**
 * The main class for the core plugin.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class AlpineCore extends AlpinePlugin {
    // This is how we prefer singletons be created, but nothing will force you to do it like this
    @Getter
    private static AlpineCore instance;
    {
        instance = this;
    }
}
