package co.crystaldev.alpinecore.handler;

import co.crystaldev.alpinecore.AlpineCore;
import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.AlpinePluginConfig;
import co.crystaldev.alpinecore.util.Messaging;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

/**
 * @author BestBearr
 * @since 0.2.0
 */
@RequiredArgsConstructor
public final class InvalidCommandUsageHandler implements InvalidUsageHandler<CommandSender> {

    private final AlpinePlugin plugin;

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {

        InvalidUsageHandler<CommandSender> handler = AlpineCore.getInstance().getInvalidCommandUsageHandler();
        if (handler != null) {
            handler.handle(invocation, result, chain);
            return;
        }

        AlpinePluginConfig config = this.plugin.getAlpineConfig();
        CommandSender sender = invocation.sender();
        Schematic command = result.getSchematic();

        if (command.isOnlyFirst()) {
            Messaging.send(sender, config.invalidUsage.single.build(this.plugin, "syntax", command.first()));
        }
        else {
            Messaging.send(sender, config.invalidUsage.multiHeader.build(this.plugin));
            for (String syntax : command.all()) {
                Messaging.send(sender, config.invalidUsage.multiLine.build(this.plugin, "syntax", syntax));
            }
        }
    }
}
