package co.crystaldev.alpinecore.handler;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.config.AlpineCoreConfig;
import co.crystaldev.alpinecore.util.Messaging;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 01/19/2024
 */
@RequiredArgsConstructor
public final class CommandInvalidUsageHandler implements InvalidUsageHandler<CommandSender> {

    private final AlpinePlugin plugin;

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        AlpineCoreConfig config = plugin.getConfigManager().getConfig(AlpineCoreConfig.class);
        CommandSender sender = invocation.sender();
        Schematic command = result.getSchematic();

        if (command.isOnlyFirst()) {
            Messaging.send(sender, config.invalidUsage.single.build("syntax", command.first()));
        }
        else {
            Messaging.send(sender, config.invalidUsage.multiHeader.build());
            for (String syntax : command.all()) {
                Messaging.send(sender, config.invalidUsage.multiLine.build("syntax", syntax));
            }
        }
    }
}
