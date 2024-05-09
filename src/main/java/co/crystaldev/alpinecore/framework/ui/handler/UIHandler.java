package co.crystaldev.alpinecore.framework.ui.handler;

import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public interface UIHandler {
    void registerEvents(@NotNull UIContext context, @NotNull UIEventBus eventBus);

    void init(@NotNull UIContext context);

    void closed(@NotNull UIContext context);
}
