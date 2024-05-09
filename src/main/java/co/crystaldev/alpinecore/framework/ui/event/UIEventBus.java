package co.crystaldev.alpinecore.framework.ui.event;

import co.crystaldev.alpinecore.framework.ui.UIContext;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A class that facilitates event handling for user interface events.
 *
 * @since 0.4.0
 */
public final class UIEventBus {

    private final Map<Class<? extends UIEvent>, List<RegisteredEventCallback<?>>> registeredEvents = new ConcurrentHashMap<>();

    /**
     * Calls the registered event callback methods for the given UIContext and UIEvent.
     *
     * @param <T> a type that extends UIEvent
     *
     * @param context the UIContext object
     * @param event   the UIEvent object
     * @return the ActionResult object representing the result of the event callback methods
     */
    @NotNull
    public <T extends UIEvent> ActionResult call(@NotNull UIContext context, @NotNull T event) {
        List<RegisteredEventCallback<T>> callbacks = (List) this.registeredEvents.get(event.getClass());
        if (callbacks != null) {
            for (RegisteredEventCallback<T> callback : callbacks) {
                ActionResult result = callback.callback.invoke(context, event);
                Validate.notNull(result, "UIEvent result cannot not be null");

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        }

        return ActionResult.PASS;
    }

    /**
     * Registers a callback for handling UI events of the specified type.
     *
     * @param <T> the event type
     *
     * @param type     the event class
     * @param priority the priority of the callback (lower value indicates higher priority)
     * @param callback the callback function to be registered
     */
    public <T extends UIEvent> void register(@NotNull Class<T> type, byte priority, @NotNull EventCallback<T> callback) {
        List<RegisteredEventCallback<T>> callbacks = (List) this.registeredEvents.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());
        callbacks.add(new RegisteredEventCallback<>(callback, priority));
        callbacks.sort(Comparator.comparing(v -> v.priority));
    }

    /**
     * Registers a callback for handling UI events of the specified type.
     *
     * @param <T> the event type
     *
     * @param type     the event class
     * @param callback the callback function to be registered
     */
    public <T extends UIEvent> void register(@NotNull Class<T> type, @NotNull EventCallback<T> callback) {
        this.register(type, UIEventPriority.NORMAL, callback);
    }

    /**
     * Clears all registered events in the UIEventBus.
     */
    public void clear() {
        this.registeredEvents.clear();
    }
}
