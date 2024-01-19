package co.crystaldev.alpinecore.handler;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.config.LiteCommandsConfig;
import co.crystaldev.alpinecore.util.Components;
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
        LiteCommandsConfig config = plugin.getConfigManager().getConfig(LiteCommandsConfig.class);
        CommandSender sender = invocation.sender();
        Schematic command = result.getSchematic();

        if (command.isOnlyFirst()) {
            Components.send(sender, config.invalidUsage.single.build("syntax", command.first()));
        }
        else {
            Components.send(sender, config.invalidUsage.multiHeader.build());
            for (String syntax : command.all()) {
                Components.send(sender, config.invalidUsage.multiLine.build("syntax", syntax));
            }
        }
    }
}
