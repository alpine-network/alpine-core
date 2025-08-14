package co.crystaldev.alpinecore.framework.command;

import co.crystaldev.alpinecore.AlpineCore;
import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author BestBearr
 * @since 0.3.0
 */
@AllArgsConstructor @Getter
public abstract class AlpineArgumentResolver<T> extends ArgumentResolver<CommandSender, T> implements Activatable {

    private final @NotNull Class<T> type;

    private final @Nullable String key;

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        // ArgumentResolvers should be shared across plugin instances
        AlpineCore.getInstance().registerArgumentResolver(context, this);
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        AlpineCore.getInstance().unregisterArgumentResolvers(context);
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
