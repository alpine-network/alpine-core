package co.crystaldev.alpinecore.framework.ui.event;

import lombok.AllArgsConstructor;

/**
 * @since 0.4.0
 */
@AllArgsConstructor
final class RegisteredEventCallback<T extends UIEvent> {
    final Object source;
    final EventCallback<T> callback;
    final byte priority;
}
