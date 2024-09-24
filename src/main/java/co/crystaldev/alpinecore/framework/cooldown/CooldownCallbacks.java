package co.crystaldev.alpinecore.framework.cooldown;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * @since 0.4.5
 */
@Getter @Setter
@ApiStatus.Internal
final class CooldownCallbacks<T> {
    private Consumer<Cooldown<T>> onInit = ctx -> {};
    private Consumer<Cooldown<T>> onMove = ctx -> {};
    private Consumer<Cooldown<T>> onCountdown = ctx -> {};
    private Consumer<Cooldown<T>> onComplete = ctx -> {};
    private Consumer<Cooldown<T>> onCancel = ctx -> {};
}
