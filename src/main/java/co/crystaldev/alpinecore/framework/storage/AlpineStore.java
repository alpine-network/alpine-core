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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
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
     * Get data stored at a given key, or create
     * an entry if there is none.
     *
     * @param key the key
     * @param defaultDataSupplier the data to create a new entry with
     * @return the data
     */
    @NotNull
    public final D getOrCreate(@NotNull K key, @NotNull Supplier<D> defaultDataSupplier) {
        if (!this.has(key)) {
            this.put(key, defaultDataSupplier.get());
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
     * Retrieve all stored data entries from the underlying data storage.
     * <p>
     * It may be a blocking task, and the time it takes to
     * complete depends on the size of the data storage.
     *
     * @see AlpineDriver#getAllEntries()
     * @return A collection containing all stored data entries.
     * @throws Exception If an exception occurs while retrieving the data entries.
     */
    @NotNull
    public final Collection<D> loadAllEntries() throws Exception {
        return this.driver.getAllEntries();
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

    /**
     * Persists cached data entries to the underlying data storage.
     * <p>
     * This method is responsible for persisting data entries that have been cached
     * but not yet saved to the underlying data storage. It attempts to persist the
     * entries and clears the cache.
     *
     * @return whether the operation was successful
     */
    public boolean flush() {
        boolean success = true;
        if (!this.driver.persistEntries(this.writeCache)) {
            this.plugin.log(Level.SEVERE, String.format("&cError persisting value in %s", this.getClass().getSimpleName()));
            success = false;
        }
        this.writeCache.clear();
        return success;
    }

    /**
     * Persists a cached data entry associated with the given key to the underlying data storage.
     * <p>
     * This method is responsible for persisting a specific data entry that has been cached
     * but not yet saved to the underlying data storage. It attempts to persist the entry and
     * returns whether the operation was successful. If the persistence operation fails, it logs
     * an error message.
     *
     * @param key The key associated with the data entry to be persisted.
     * @return Whether the persistence operation was successful.
     */
    public boolean flush(@NotNull K key) {
        D value = this.writeCache.remove(key);
        if (value == null) {
            return false;
        }

        boolean success = true;
        if (!this.driver.persistEntry(key, value)) {
            this.plugin.log(Level.SEVERE, String.format("&cError persisting value \"%s\" in %s", key, this.getClass().getSimpleName()));
            success = false;
        }
        return success;
    }

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this::flush, 1L, PERSIST_TASK_PERIOD);

        if (this.taskId != -1)
            this.plugin.log(String.format("&aStore activated &d%s", this.getClass().getSimpleName()));
        else
            this.plugin.log(Level.SEVERE, String.format("&cError activating &d%s", this.getClass().getSimpleName()));
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        Bukkit.getScheduler().cancelTask(this.taskId);
        this.flush();
        this.readCache.invalidateAll();
        this.taskId = -1;
        this.plugin.log(String.format("&cStore deactivated &d%s", this.getClass().getSimpleName()));
    }

    @Override
    public final boolean isActive() {
        return this.taskId != -1;
    }
}
