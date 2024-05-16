package dev.tomwmth.exampleplugin.ui;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.framework.ui.event.ActionResult;
import co.crystaldev.alpinecore.framework.ui.handler.GenericUIHandler;
import co.crystaldev.alpinecore.framework.ui.interaction.DropContext;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.alpinecore.util.Messaging;
import dev.tomwmth.exampleplugin.config.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
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
    public @Nullable Element createElement(@NotNull UIContext context, @NotNull String key, @Nullable DefinedConfigItem definition) {
        if (key.equals("storage")) {
            return new MutableElement(context);
        }

        return super.createElement(context, key, definition);
    }

    @Override
    public void dropped(@NotNull UIContext context, @NotNull DropContext drop) {
        Config config = context.plugin().getConfiguration(Config.class);

        ItemStack item = drop.item();
        int amount = drop.amount();
        if (amount == 11) {
            drop.result(ActionResult.CANCEL);

            Messaging.send(context.player(), config.actionMessage.build(context.plugin(),
                    "action", "Cancelled drop"));
        }
        else {
            Component formattedItem = drop.hasItem()
                    ? Component.text("Dropped " + amount + "x " + item.getType())
                    : Component.text("< didn't drop item >");

            Messaging.send(context.player(), config.actionMessage.build(context.plugin(),
                    "action", formattedItem));
        }
    }
}
