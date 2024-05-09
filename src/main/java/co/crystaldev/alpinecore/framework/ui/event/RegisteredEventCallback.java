package co.crystaldev.alpinecore.framework.ui.event;

import lombok.AllArgsConstructor;

/**
 * @since 0.4.0
 */
@AllArgsConstructor
final class RegisteredEventCallback<T extends UIEvent> {
    public final EventCallback<T> callback;
    public final byte priority;
}
