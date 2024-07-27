package co.crystaldev.alpinecore.framework.teleport;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * @since 0.4.0
 */
@Getter @Setter
@ApiStatus.Internal
final class TeleportCallbacks {

    private static final Consumer<TeleportContext> NO_OP = ctx -> {};

    private Consumer<TeleportContext> onInit = NO_OP;

    private Consumer<TeleportContext> onMove = NO_OP;

    private Consumer<TeleportContext> onCountdown = NO_OP;

    private Consumer<TeleportContext> onTeleport = NO_OP;

    private Consumer<TeleportContext> onCancel = NO_OP;
}
