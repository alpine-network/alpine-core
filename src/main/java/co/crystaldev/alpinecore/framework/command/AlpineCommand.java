package co.crystaldev.alpinecore.framework.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper around LiteCommands to allow for
 * automatic activation by the framework.
 * <p>
 * Inheritors should never be manually instantiated.
 *
 * @see <a href="https://litedevelopers.github.io/LiteDevelopers-documentation/introdution.html">LiteCommands Wiki</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineCommand implements Activatable {
    /** The plugin that activated this command */
    protected final AlpinePlugin plugin;

    @Getter
    private boolean active;

    /**
     * Locked down to prevent improper instantiation.
     * <p>
     * Commands are reflectively instantiated by the
     * framework automatically.
     */
    protected AlpineCommand(@NotNull AlpinePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called to register command-specific conditions and suggestions.
     *
     * @param builder The LiteCommands builder.
     */
    public void setupCommandManager(@NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder) {
        // NO-OP
    }

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        this.active = true;
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        throw new UnsupportedOperationException("commands can not be deactivated");
    }

    @Override
    public final boolean canDeactivate() {
        return false;
    }
}
