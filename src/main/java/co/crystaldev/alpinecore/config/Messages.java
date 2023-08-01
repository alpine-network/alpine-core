package co.crystaldev.alpinecore.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;

/**
 * Configuration for holding plugin messages.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class Messages extends AlpineConfig {
    /** Prefix to be prepended to plugin messages. */
    public ConfigMessage prefix = new ConfigMessage("<dark_gray>[</dark_gray><gradient:#4895ef:#4cc9f0>AlpineCore</gradient><dark_gray>]</dark_gray>");
}
