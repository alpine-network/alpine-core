package co.crystaldev.alpinecore.framework.ui.event;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a class that subscribes to UI events and registers them to a UI event bus.
 *
 * @since 0.4.0
 */
public interface UIEventSubscriber {
    /**
     * Registers the events of a UIContext to a UIEventBus.
     *
     * @param context the UIContext object to register events for
     * @param bus     the UIEventBus object to register the events to
     */
    void registerEvents(@NotNull UIContext context, @NotNull UIEventBus bus);
}
