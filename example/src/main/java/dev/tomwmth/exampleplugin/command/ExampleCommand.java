package dev.tomwmth.exampleplugin.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.framework.ui.type.InventoryUI;
import co.crystaldev.alpinecore.util.Messaging;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import dev.tomwmth.exampleplugin.config.Config;
import dev.tomwmth.exampleplugin.storage.Statistics;
import dev.tomwmth.exampleplugin.storage.StatisticsStore;
import dev.tomwmth.exampleplugin.ui.DemoUIHandler;
import dev.tomwmth.exampleplugin.ui.MutableUIHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@Command(name = "example")
@Description("This is an example command!")
@Permission("exampleplugin.example")
public class ExampleCommand extends AlpineCommand {
    protected ExampleCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context CommandSender sender, @Arg("target") @Key("lookingAtPlayer") Player target) {
        Config config = this.plugin.getConfigManager().getConfig(Config.class);

        StatisticsStore store = StatisticsStore.getInstance();
        Statistics stats = store.getOrCreate(target.getPlayer(), new Statistics());

        Messaging.send(sender,
                config.commandMessage.build(this.plugin,
                        "player", target.getPlayer().getName(),
                        "amount", stats.blocksBroken)
        );
    }

    @Execute(name = "subcommand")
    public void executeSubcommand(@Context Player sender, @Arg("action") ExampleAction action) {
        Config config = this.plugin.getConfigManager().getConfig(Config.class);
        World world = sender.getWorld();
        String message;
        switch (action) {
            case SPAWN_COW:
                message = "Spawn Cow";
                world.spawnEntity(sender.getLocation(), EntityType.COW);
                break;
            case SPAWN_PIG:
                message = "Spawn Pig";
                world.spawnEntity(sender.getLocation(), EntityType.PIG);
                break;
            default:
                message = "Boom!";
                world.createExplosion(sender.getLocation(), 4.0F);
        }

        Messaging.send(sender,
                config.actionMessage.build(this.plugin,
                        "action", message)
        );
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

    private enum ExampleAction {
        SPAWN_PIG,
        SPAWN_COW,
        EXPLODE
    }

    // `AlpineArgumentResolver<TYPE>` is automatically discovered and instantiated
    private static final class LookingAtPlayersArgument extends AlpineArgumentResolver<Player> {
        public LookingAtPlayersArgument() {
            super(Player.class, "lookingAtPlayer");
        }

        @Override
        protected ParseResult<Player> parse(Invocation<CommandSender> invocation, Argument<Player> context, String argument) {
            CommandSender sender = invocation.sender();
            if (!(sender instanceof Player)) {
                // Sender is not able to visualize any players
                return ParseResult.failure("you are not a player");
            }

            Player resolved = Bukkit.getPlayer(argument);
            if (resolved == null) {
                // No player was found
                return ParseResult.failure("player not found");
            }

            if (!resolved.canSee((Player) sender)) {
                // No visible player was found
                return ParseResult.failure("player is not visible");
            }

            return ParseResult.success(resolved);
        }

        @Override
        public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Player> argument, SuggestionContext context) {
            CommandSender sender = invocation.sender();
            if (!(sender instanceof Player)) {
                // Sender is not able to visualize any players
                return SuggestionResult.of();
            }

            List<String> visiblePlayers = Bukkit.getOnlinePlayers()
                    .stream()
                    // ensure the name is reflecting what the player typed
                    .filter(p -> p.getName().startsWith(context.getCurrent().toString()))
                    // ensure the sender can see the player
                    .filter(p -> ((Player) sender).canSee(p))
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());

            return SuggestionResult.of(visiblePlayers.toArray(new String[0]));
        }
    }
}
