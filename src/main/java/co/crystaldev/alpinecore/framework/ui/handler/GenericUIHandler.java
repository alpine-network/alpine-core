package co.crystaldev.alpinecore.framework.ui.handler;

import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import co.crystaldev.alpinecore.framework.ui.event.UIEventBus;
import co.crystaldev.alpinecore.framework.ui.event.UIEventPriority;
import co.crystaldev.alpinecore.framework.ui.event.type.DragEvent;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public final class GenericUIHandler implements UIHandler {

    private static final GenericUIHandler INSTANCE = new GenericUIHandler();

    @Override
    public void registerEvents(@NotNull UIContext context, @NotNull UIEventBus eventBus) {
        // Do not allow dragging in the inventory
        eventBus.register(DragEvent.class, UIEventPriority.LOWEST, (ctx, event) -> {
            return ActionResult.CANCEL;
        });
    }

    @Override
    public void init(@NotNull UIContext context) {
        // NO OP
    }

    @Override
    public void closed(@NotNull UIContext context) {
        // NO OP
    }

    @NotNull
    public static GenericUIHandler getInstance() {
        return INSTANCE;
    }
}
