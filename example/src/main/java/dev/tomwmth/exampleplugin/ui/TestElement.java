package dev.tomwmth.exampleplugin.ui;

import co.crystaldev.alpinecore.framework.ui.GuiType;
import co.crystaldev.alpinecore.framework.ui.UIContext;
import co.crystaldev.alpinecore.framework.ui.element.ClickContext;
import co.crystaldev.alpinecore.framework.ui.element.ClickProperties;
import co.crystaldev.alpinecore.framework.ui.element.Element;
import co.crystaldev.alpinecore.util.Messaging;
import dev.tomwmth.exampleplugin.config.Config;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.4.0
 */
public final class TestElement extends Element {

    public TestElement(@NotNull UIContext context) {
        super(context);
        this.clickProperties = ClickProperties.ALL_ALLOWED;
    }

    @Override
    public void clicked(@NotNull ClickContext context) {
        super.clicked(context);

        System.out.println("Interacted @ " + this.position.getSlot());

        if (context.hasItem()) {
            Config config = this.context.plugin().getConfiguration(Config.class);
            Messaging.send(this.context.player(), config.actionMessage.build(this.context.plugin(),
                    "action", "Interacted with an element!"));
        }
    }

    @Override
    public @Nullable ItemStack buildItemStack() {
        return null;
    }

    @Override
    public boolean canTransferItems() {
        return true;
    }
}
