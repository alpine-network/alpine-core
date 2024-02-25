package co.crystaldev.alpinecore.framework.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 0.3.0
 */
@AllArgsConstructor @Getter
public abstract class AlpineArgumentResolver<T> extends ArgumentResolver<CommandSender, T> implements Activatable {

    private final @NotNull Class<T> type;

    private final @NotNull String key;

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        // NO-OP
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        // NO-OP
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
