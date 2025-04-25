package co.crystaldev.alpinecore.framework.teleport;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a teleport handler that handles different phases of the teleportation process.
 * Implementations of this interface can provide custom logic for teleporting players across
 * all other registered {@link co.crystaldev.alpinecore.AlpinePlugin AlpinePlugins}.
 *
 * @since 0.4.0
 */
public interface TeleportHandler {

    /**
     * Initializes the teleport process with the given context.
     *
     * @param context the context containing information about the teleportation
     */
    void onInit(@NotNull TeleportContext context);


    /**
     * Applies the teleport process with the given context.
     *
     * @param context the context containing information about the teleportation
     */
    void onApply(@NotNull TeleportContext context);

    /**
     * Handles the countdown phase of the teleport process.
     *
     * @param context the context containing information about the teleportation
     */
    void onCountdown(@NotNull TeleportContext context);

    /**
     * Handles the teleport phase of the teleport process.
     *
     * @param context the context containing information about the teleportation
     */
    void onTeleport(@NotNull TeleportContext context);

    /**
     * Handles player movement during the teleport process.
     *
     * @param context the context containing information about the teleportation
     */
    void onMove(@NotNull TeleportContext context);

    /**
     * Handles player damage during the teleport process.
     *
     * @param context the context containing information about the teleportation
     */
    void onDamage(@NotNull TeleportContext context);

    /**
     * Handles the cancellation of the teleport process.
     *
     * @param context the context containing information about the teleportation
     */
    void onCancel(@NotNull TeleportContext context);
}
