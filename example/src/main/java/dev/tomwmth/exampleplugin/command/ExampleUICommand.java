package dev.tomwmth.exampleplugin.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.framework.ui.type.InventoryUI;
import co.crystaldev.alpinecore.util.Messaging;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.tomwmth.exampleplugin.config.Config;
import dev.tomwmth.exampleplugin.ui.DemoUIHandler;
import dev.tomwmth.exampleplugin.ui.MutableUIHandler;
import dev.tomwmth.exampleplugin.ui.StackedUIHandler;
import org.bukkit.entity.Player;

/**
 * @since 0.4.0
 */
@Command(name = "exampleui")
@Description("This is an example command!")
@Permission("exampleplugin.exampleui")
public final class ExampleUICommand extends AlpineCommand {
    protected ExampleUICommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "demoui")
    public void executeDemoUI(@Context Player sender) {
        Config config = this.plugin.getConfigManager().getConfig(Config.class);
        InventoryUI ui = config.paginatedUI.build(this.plugin, DemoUIHandler.getInstance());
        ui.view(sender);

        Messaging.send(sender,
                config.actionMessage.build(this.plugin,
                        "action", "Opened Paginated Demo Inventory UI")
        );
    }

    @Execute(name = "mutableui")
    public void executeMutableUI(@Context Player sender) {
        Config config = this.plugin.getConfigManager().getConfig(Config.class);
        InventoryUI ui = config.mutableUI.build(this.plugin, MutableUIHandler.getInstance());
        ui.view(sender);

        Messaging.send(sender,
                config.actionMessage.build(this.plugin,
                        "action", "Opened Mutable Demo Inventory UI")
        );
    }

    @Execute(name = "stackui")
    public void executeStackUI(@Context Player sender) {
        Config config = this.plugin.getConfigManager().getConfig(Config.class);
        InventoryUI ui = StackedUIHandler.UI_SCREENS.get(0).build(this.plugin, new StackedUIHandler(0));
        ui.view(sender);

        Messaging.send(sender,
                config.actionMessage.build(this.plugin,
                        "action", "Opened Stacked Demo Inventory UI")
        );
    }
}
