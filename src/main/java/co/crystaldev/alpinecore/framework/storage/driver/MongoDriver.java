package co.crystaldev.alpinecore.framework.storage.driver;

import co.crystaldev.alpinecore.AlpinePlugin;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Thomas Wearmouth
 */
@ApiStatus.Experimental
public class MongoDriver<K, D> extends AlpineDriver<K, D> {

    public MongoDriver(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean persistEntry(@NotNull K key, @NotNull D data) {
        throw new NotImplementedException();
    }

    @Override
    public boolean deleteEntry(@NotNull K key) {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasEntry(@NotNull K key) {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull D retrieveEntry(@NotNull K key) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull Collection<D> getAllEntries() throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull Collection<D> getAllEntries(@Nullable Consumer<Exception> exceptionConsumer) {
        throw new NotImplementedException();
    }
}
