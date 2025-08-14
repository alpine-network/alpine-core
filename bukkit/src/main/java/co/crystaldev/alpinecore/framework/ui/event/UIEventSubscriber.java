package co.crystaldev.alpinecore.framework.ui.event;

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
     * @param bus the UIEventBus object to register the events to
     */
    void registerEvents(@NotNull UIEventBus bus);
}
