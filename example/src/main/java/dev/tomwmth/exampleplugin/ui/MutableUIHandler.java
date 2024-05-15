package dev.tomwmth.exampleplugin.ui;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.handler.GenericUIHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.4.0
 */
public final class MutableUIHandler extends GenericUIHandler {

    private static final MutableUIHandler INSTANCE = new MutableUIHandler();

    @NotNull
    public static MutableUIHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable Element createEntry(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem definition) {
        if (key.equals("storage")) {
            return new TestElement(context);
        }

        return super.createEntry(context, key, definition);
    }
}
