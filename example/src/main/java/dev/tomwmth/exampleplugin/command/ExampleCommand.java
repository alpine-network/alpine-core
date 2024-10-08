package dev.tomwmth.exampleplugin.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.framework.cooldown.Cooldown;
import co.crystaldev.alpinecore.framework.cooldown.CooldownHandler;
import co.crystaldev.alpinecore.framework.teleport.TeleportTask;
import co.crystaldev.alpinecore.util.Components;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@Command(name = "example")
@Description("This is an example command!")
@Permission("exampleplugin.example")
public class ExampleCommand extends AlpineCommand {

    private final CooldownHandler<Player> warmupHandler;

    protected ExampleCommand(AlpinePlugin plugin) {
        super(plugin);
        this.warmupHandler = CooldownHandler.<Player>builder()
                .delay(5, TimeUnit.SECONDS)
                .build(plugin);
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

    @Execute(name = "warmup")
    public void executeWarmup(@Context Player sender) {
        Cooldown<Player> warmup = this.warmupHandler.testWarmup(sender, ctx -> {
            // Warmup has finished
            ctx.message(Component.text("Done!"));
        });

        Messaging.send(sender, Components.joinSpaces(
                Component.text("Will execute in"),
                Component.text(warmup.remainingTime(TimeUnit.SECONDS) + " second(s)").color(TextColor.color(0x56CAFF))
        ));
    }

    @Execute(name = "cooldown")
    public void executeCooldown(@Context Player sender) {
        Cooldown<Player> cooldown = this.warmupHandler.testCooldown(sender);
        if (cooldown.isActive()) {
            // Player is on cooldown
            Messaging.send(sender, Components.joinSpaces(
                    Component.text("You are on cooldown for"),
                    Component.text(cooldown.remainingTime(TimeUnit.SECONDS) + " second(s)").color(TextColor.color(0x56CAFF))
            ));
            return;
        }

        Messaging.send(sender, Component.text("Done!"));
    }

    @Execute(name = "move")
    public void move(@Context Player sender, @Arg("offset_x") int x, @Arg("offset_y") int y, @Arg("offset_z") int z) {

        Location destination = sender.getLocation().clone().add(x, y, z);
        TeleportTask teleport = TeleportTask.builder(sender, destination)
                .delay(5, TimeUnit.SECONDS)
                .resetPitchAndYaw()
                .onInit(ctx -> {
                    ctx.message(Components.joinSpaces(
                            Component.text("Teleporting to").color(TextColor.color(0xA6D1FD)),
                            Component.text(destination.getBlockX() + ", " + destination.getBlockY() + ", " + destination.getBlockZ()).color(TextColor.color(0x56CAFF))
                    ));
                })
                .onCountdown(ctx -> {
                    ctx.message(Components.joinSpaces(
                            Component.text("Teleporting in:").color(TextColor.color(0xA6D1FD)),
                            Component.text(ctx.timeUntilTeleport(TimeUnit.SECONDS) + " second(s)").color(TextColor.color(0x56CAFF))
                    ));
                })
                .onTeleport(ctx -> {
                    ctx.message(Component.text("Teleporting...").color(TextColor.color(0xA6D1FD)));
                })
                .onMove(ctx -> {
                    ctx.message(Component.text("Cancelled Teleportation due to movement").color(TextColor.color(0xFD3115)));
                })
                .build();

        this.plugin.getTeleportManager().initiateTeleport(teleport);
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
