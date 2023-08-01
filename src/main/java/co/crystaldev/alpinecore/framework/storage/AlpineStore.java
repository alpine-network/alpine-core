package co.crystaldev.alpinecore.framework.storage;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import co.crystaldev.alpinecore.framework.storage.driver.AlpineDriver;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Handles persistent key and data pairs which are
 * backed by a configurable {@link AlpineDriver}.
 * <p>
 * Inheritors should never be manually instantiated.
 *
 * @param <K> the key type
 * @param <D> the data type
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@ApiStatus.Experimental
@SuppressWarnings("UnstableApiUsage")
public abstract class AlpineStore<K, D> implements Activatable {
    // TODO: allow plugins to change this?
    private static final long PERSIST_TASK_PERIOD = 3600L; // ~3m in ticks

    /** The plugin that activated this store */
    protected final AlpinePlugin plugin;

    private final AlpineDriver<K, D> driver;
    private final LoadingCache<K, D> readCache;
    private final Map<K, D> writeCache;

    private int taskId;

    /**
     * Simple constructor using the default caching strategy.
     * <p>
     * Locked down to prevent improper instantiation.
     * <p>
     * Stores are reflectively instantiated by the
     * framework automatically.
     *
     * @param driver the storage driver
     * @since 0.1.0
     */
    protected AlpineStore(AlpinePlugin plugin, AlpineDriver<K, D> driver) {
        this(plugin, driver, CachingStrategy.builder().build());
    }

    /**
     * Constructor that allows the configuration of a caching strategy.
     * <p>
     * Locked down to prevent improper instantiation.
     * <p>
     * Stores are reflectively instantiated by the
     * framework automatically.
     *
     * @param driver the storage driver
     * @param strategy the strategy used by the caching layer
     * @since 0.1.0
     */
    protected AlpineStore(AlpinePlugin plugin, AlpineDriver<K, D> driver, CachingStrategy strategy) {
        this.plugin = plugin;
        this.driver = driver;
        this.readCache = CacheBuilder.newBuilder()
                .maximumSize(strategy.getMaximumSize())
                .expireAfterAccess(strategy.getExpireTimeValue(), strategy.getExpireTimeUnit())
                .concurrencyLevel(strategy.getConcurrencyLevel())
                .build(new CacheLoader<K, D>() {
            @Override
            public D load(K key) throws Exception {
                if (AlpineStore.this.writeCache.containsKey(key))
                    return AlpineStore.this.writeCache.get(key);
                else
                    return AlpineStore.this.driver.retrieveEntry(key);
            }
        });
        this.writeCache = new HashMap<>();
    }

    /**
     * Get data stored at a given key.
     *
     * @param key the key
     * @return the data
     */
    @Nullable
    public final D get(@NotNull K key) {
        try {
            // This call will only be expensive if the entry is uncached
            return this.readCache.get(key);
        }
        catch (Throwable t) {
            this.plugin.log(String.format("Error getting value for key %s", key), t);
        }
        return null;
    }

    /**
     * Get data stored at a given key, or create
     * an entry if there is none.
     *
     * @param key the key
     * @param defaultData the data to create a new entry with
     * @return the data
     */
    @NotNull
    public final D getOrCreate(@NotNull K key, @NotNull D defaultData) {
        if (!this.has(key)) {
            this.put(key, defaultData);
        }
        D data = this.get(key);
        if (data != null) {
            return data;
        }
        else {
            // How???
            throw new IllegalStateException();
        }
    }

    /**
     * Check if data exists for a given key.
     *
     * @param key the key
     * @return whether an entry exists
     */
    public final boolean has(@NotNull K key) {
        if (this.writeCache.containsKey(key))
            return true;
        else if (this.readCache.asMap().containsKey(key))
            return true;
        else
            return this.driver.hasEntry(key);
    }

    /**
     * Delete data stored at a given key.
     *
     * @param key the key
     * @return whether the operation was successful
     */
    public final boolean remove(@NotNull K key) {
        this.writeCache.remove(key);
        this.readCache.invalidate(key);
        return this.driver.deleteEntry(key);
    }

    /**
     * Store data at a given key.
     * <p>
     * This method will silently overwrite any
     * pre-existing data.
     *
     * @param key the key
     * @param data the data
     */
    public final void put(@NotNull K key, @NotNull D data) {
        this.writeCache.put(key, data);
        this.readCache.refresh(key);
    }

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this::persistCache, 1L, PERSIST_TASK_PERIOD);

        if (this.taskId != -1)
            this.plugin.log(String.format("&aStore activated &d%s", this.getClass().getSimpleName()));
        else
            this.plugin.log(Level.SEVERE, String.format("&cError activating &d%s", this.getClass().getSimpleName()));
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        Bukkit.getScheduler().cancelTask(this.taskId);
        this.persistCache();
        this.readCache.invalidateAll();
        this.taskId = -1;
        this.plugin.log(String.format("&cStore deactivated &d%s", this.getClass().getSimpleName()));
    }

    @Override
    public final boolean isActive() {
        return this.taskId != -1;
    }

    private void persistCache() {
        for (Map.Entry<K, D> entry : this.writeCache.entrySet()) {
            boolean success = this.driver.persistEntry(entry.getKey(), entry.getValue());
            if (!success) {
                this.plugin.log(Level.SEVERE, String.format("&cError persisting key \"%s\" in %s", entry.getKey(), this.getClass().getSimpleName()));
            }
        }
        this.writeCache.clear();
    }
}
