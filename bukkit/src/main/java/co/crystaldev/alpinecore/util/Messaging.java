package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.Reference;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility for sending recipients Adventure components.
 *
 * @author BestBearr
 * @since 0.3.0
 */
@UtilityClass
public final class Messaging {

    /**
     * Sends a message to the specified CommandSender.
     *
     * @param sender      the recipient of the message
     * @param messageType the type of the message
     * @param component   the message to send
     */
    public static void send(@NotNull CommandSender sender, @NotNull MessageType messageType, @Nullable Component component) {
        if (component == null) {
            return;
        }

        switch (messageType) {
            case CHAT:
                send(sender, component);
                break;

            case ACTION_BAR:
                actionBar(sender, component);
        }
    }

    /**
     * Sends a message to the specified CommandSender.
     *
     * @param sender      the recipient of the message
     * @param messageType the type of the message
     * @param components  the message to send
     */
    public static void send(@NotNull CommandSender sender, @NotNull MessageType messageType, @NotNull Component... components) {
        if (components == null || components.length == 0) {
            return;
        }

        switch (messageType) {
            case CHAT:
                send(sender, components);
                break;

            case ACTION_BAR:
                actionBar(sender, components);
        }
    }

    /**
     * Sends messages to a collection of CommandSenders, each customized by a function.
     *
     * @param senders           the recipients of the messages
     * @param messageType       the type of the message
     * @param componentFunction a function that supplies the message for each sender
     */
    public static void send(@NotNull Collection<CommandSender> senders, @NotNull MessageType messageType,
                            @NotNull Function<@NotNull CommandSender, @Nullable Component> componentFunction) {
        if (messageType == MessageType.DISABLED) {
            return;
        }

        for (CommandSender sender : senders) {
            Component component = componentFunction.apply(sender);
            if (component != null) {
                send(sender, messageType, component);
            }
        }
    }

    /**
     * Sends a message to the specified CommandSender.
     *
     * @param sender    the recipient of the message
     * @param component the message to send
     */
    public static void send(@NotNull CommandSender sender, @Nullable Component component) {
        if (component == null) {
            return;
        }

        if (sender instanceof Audience) {
            ((Audience) sender).sendMessage(component);
        }
        else {
            wrap(sender).sendMessage(component);
        }
    }

    /**
     * Sends a message consisting of multiple components to the specified CommandSender.
     *
     * @param sender     the recipient of the message
     * @param components the components to send
     */
    public static void send(@NotNull CommandSender sender, @NotNull Component... components) {
        send(sender, Components.joinNewLines(components));
    }

    /**
     * Sends messages to a collection of CommandSenders, each customized by a function.
     *
     * @param senders           the recipients of the messages
     * @param componentFunction a function that supplies the message for each sender
     */
    public static void send(@NotNull Collection<CommandSender> senders,
                            @NotNull Function<@NotNull CommandSender, @Nullable Component> componentFunction) {
        for (CommandSender sender : senders) {
            Component component = componentFunction.apply(sender);
            if (component != null) {
                wrap(sender).sendMessage(component);
            }
        }
    }

    /**
     * Sends a message to an OfflinePlayer if they are currently online.
     *
     * @param sender    the intended recipient of the message
     * @param component the message to send; can be null
     */
    public static void attemptSend(@NotNull OfflinePlayer sender, @Nullable Component component) {
        if (component == null || !sender.isOnline()) {
            return;
        }

        if (sender instanceof Audience) {
            ((Audience) sender).sendMessage(component);
        }
        else if (sender instanceof CommandSender) {
            wrap((CommandSender) sender).sendMessage(component);
        }
    }

    /**
     * Sends a message consisting of multiple components to an OfflinePlayer if they are currently online.
     *
     * @param sender     the intended recipient of the message
     * @param components the components to send
     */
    public static void attemptSend(@NotNull OfflinePlayer sender, @NotNull Component... components) {
        attemptSend(sender, Components.joinNewLines(components));
    }

    /**
     * Sends messages to a collection of OfflinePlayers, each customized by a function, if they are currently online.
     *
     * @param players           the intended recipients of the messages
     * @param componentFunction a function that supplies the message for each player
     */
    public static void attemptSend(@NotNull Collection<OfflinePlayer> players,
                                   @NotNull Function<@NotNull Player, @Nullable Component> componentFunction) {
        for (OfflinePlayer offlinePlayer : players) {
            if (!offlinePlayer.isOnline())
                continue;

            Player player = offlinePlayer.getPlayer();
            Component component = componentFunction.apply(player);
            if (component != null) {
                wrap(player).sendMessage(component);
            }
        }
    }

    /**
     * Broadcasts a message to all online players and the console.
     *
     * @param component the message to broadcast; can be null
     */
    public static void broadcast(@Nullable Component component) {
        if (component == null)
            return;

        // notify the console
        wrap(Bukkit.getServer().getConsoleSender()).sendMessage(component);

        // notify all players
        Bukkit.getOnlinePlayers().forEach(player -> send(player, component));
    }

    /**
     * Broadcasts a message consisting of multiple components to all online players and the console.
     *
     * @param components the components to broadcast
     */
    public static void broadcast(@NotNull Component... components) {
        broadcast(Components.joinNewLines(components));
    }

    /**
     * Broadcasts a message to all online players and the console, with an option to filter recipients.
     *
     * @param component       the message to broadcast
     * @param playerPredicate a predicate to determine which players should receive the message
     */
    public static void broadcast(@NotNull Component component, @NotNull Predicate<Player> playerPredicate) {
        // notify the console
        wrap(Bukkit.getServer().getConsoleSender()).sendMessage(component);

        // selectively notify all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerPredicate.test(player)) {
                send(player, component);
            }
        }
    }

    /**
     * Broadcasts a message to a specified collection of CommandSenders.
     *
     * @param senders   the recipients of the message
     * @param component the message to broadcast
     */
    public static void broadcast(@NotNull Collection<CommandSender> senders, @NotNull Component component) {
        for (CommandSender sender : senders) {
            wrap(sender).sendMessage(component);
        }
    }

    /**
     * Sends a title and subtitle to a CommandSender.
     *
     * @param sender   the recipient of the title and subtitle
     * @param title    the title component
     * @param subtitle the subtitle component
     */
    public static void title(@NotNull CommandSender sender, @NotNull Component title, @NotNull Component subtitle) {
        wrap(sender).showTitle(Title.title(title, subtitle));
    }

    /**
     * Sends an action bar message to a CommandSender.
     *
     * @param sender    the recipient of the action bar message
     * @param component the message to send
     */
    public static void actionBar(@NotNull CommandSender sender, @NotNull Component component) {
        wrap(sender).sendActionBar(component);
    }

    /**
     * Sends an action bar message to a CommandSender.
     *
     * @param sender     the recipient of the action bar message
     * @param components the message to send
     */
    public static void actionBar(@NotNull CommandSender sender, @NotNull Component... components) {
        wrap(sender).sendActionBar(Components.joinNewLines(components));
    }

    /**
     * Sends an action bar message to a collection of CommandSenders, each customized by a function.
     *
     * @param senders           the recipients of the action bar message
     * @param componentFunction a function that supplies the message for each sender
     */
    public static void actionBar(@NotNull Collection<CommandSender> senders,
                                 @NotNull Function<@NotNull CommandSender, @Nullable Component> componentFunction) {
        for (CommandSender sender : senders) {
            Component component = componentFunction.apply(sender);
            if (component != null) {
                wrap(sender).sendActionBar(component);
            }
        }
    }

    /**
     * Wraps a CommandSender in an Audience, enabling advanced messaging features.
     *
     * @param sender the CommandSender to wrap
     * @return an Audience representing the wrapped CommandSender
     */
    public static @NotNull Audience wrap(@NotNull CommandSender sender) {
        if (sender instanceof Audience) {
            return (Audience) sender;
        }
        return sender instanceof Player ? Reference.AUDIENCES.player((Player) sender) : Reference.AUDIENCES.sender(sender);
    }
}
