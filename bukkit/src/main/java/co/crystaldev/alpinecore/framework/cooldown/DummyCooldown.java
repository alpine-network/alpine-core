package co.crystaldev.alpinecore.framework.cooldown;

import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.6
 */
final class DummyCooldown<T> extends Cooldown<T> {

    private static final Cooldown<?> INSTANCE = new DummyCooldown<>();

    DummyCooldown() {
        super(null, 0, false, null);
    }

    @Override
    public void setRemainingTicks(int remainingTicks) {
        // NO OP
    }

    public static <T> @NotNull Cooldown<T> instance() {
        INSTANCE.clearMessage();
        return (Cooldown<T>) INSTANCE;
    }
}
