package dev.tomwmth.exampleplugin.command;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.Components;
import dev.tomwmth.exampleplugin.config.Config;
import dev.tomwmth.exampleplugin.storage.Statistics;
import dev.tomwmth.exampleplugin.storage.StatisticsStore;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@CommandAlias("example")
@Description("This is an example command!")
public class ExampleCommand extends AlpineCommand {
    protected ExampleCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Default @Syntax("<player>") @CommandCompletion("@player")
    public void execute(CommandSender sender, OnlinePlayer target) {
        Config config = this.plugin.getConfigManager().getConfig(Config.class);

        StatisticsStore store = StatisticsStore.getInstance();
        Statistics stats = store.getOrCreate(target.getPlayer(), new Statistics());

        Component prefix = config.prefix.build();
        Components.send(sender, prefix, config.commandMessage.build("player", target.getPlayer().getName(),
                "amount", stats.blocksBroken));
    }

    @Override
    public void registerCompletions(@NotNull PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerAsyncCompletion("player", context -> {
            Set<String> names = new HashSet<>();
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                if (context.getPlayer().canSee(player)) {
                    names.add(player.getName());
                }
            }
            return names;
        });
    }
}
