package co.crystaldev.alpinecore.framework.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper around the Annotation Command Framework to
 * allow for automatic activation by the framework.
 * <p>
 * Inheritors should never be manually instantiated.
 *
 * @see BaseCommand
 * @see <a href="https://github.com/aikar/commands/wiki">aikar/commands</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineCommand extends BaseCommand implements Activatable {
    /** The plugin that activated this command */
    protected final AlpinePlugin plugin;

    @Getter
    private boolean active = false;

    /**
     * Locked down to prevent improper instantiation.
     * <p>
     * Commands are reflectively instantiated by the
     * framework automatically.
     */
    protected AlpineCommand(AlpinePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when command conditions should be registered.
     *
     * @param commandManager The command manager
     */
    public void registerConditions(@NotNull PaperCommandManager commandManager) {
        // NO-OP
    }

    /**
     * Called when command completions should be registered.
     *
     * @param commandManager The command manager
     */
    public void registerCompletions(@NotNull PaperCommandManager commandManager) {
        // NO-OP
    }

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        PaperCommandManager commandManager = this.plugin.getCommandManager();
        commandManager.registerCommand(this);
        this.registerConditions(commandManager);
        this.registerCompletions(commandManager);
        this.active = true;
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        this.plugin.getCommandManager().unregisterCommand(this);
        this.active = false;
    }
}
