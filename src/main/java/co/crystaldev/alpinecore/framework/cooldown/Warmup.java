package co.crystaldev.alpinecore.framework.cooldown;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @since 0.4.5
 */
final class Warmup<T> extends Cooldown<T> {

    private final @NotNull Consumer<Cooldown<T>> onComplete;

    Warmup(@NotNull T entity, boolean warmup, int remainingTicks, boolean canMove, @Nullable Location origin, @NotNull Consumer<Cooldown<T>> onComplete) {
        super(entity, warmup, remainingTicks, canMove, origin);
        this.onComplete = onComplete;
    }

    @Override
    void complete() {
        this.onComplete.accept(this);
    }
}
