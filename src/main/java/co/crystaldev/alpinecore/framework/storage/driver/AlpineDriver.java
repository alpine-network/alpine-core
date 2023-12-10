package co.crystaldev.alpinecore.framework.storage.driver;

import co.crystaldev.alpinecore.AlpineCore;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * Responsible for saving key-data pairs to a
 * backend source. Used to back an {@link co.crystaldev.alpinecore.framework.storage.AlpineStore}.
 *
 * @param <K> The type of the key
 * @param <D> The type of the data
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public abstract class AlpineDriver<K, D> {

    protected static final SerializerRegistry SERIALIZER_REGISTRY = AlpineCore.getInstance().getSerializerRegistry();

    /**
     * Save data under a given key.
     * <p>
     * Any exceptions generated by this method are
     * swallowed.
     *
     * @param key The key
     * @param data The data to save
     * @return Whether the operation was successful
     */
    public abstract boolean persistEntry(@NotNull K key, @NotNull D data);

    /**
     * Save multiple data entries under their respective keys.
     * <p>
     * This method allows you to save multiple data entries at once by providing a
     * map of key-value pairs. It iterates through the map and calls the
     * {@link #persistEntry(Object, Object)} method for each entry. Any exceptions
     * generated during the saving process are swallowed for individual entries.
     *
     * @param entries A map containing key-value pairs to be saved.
     */
    public boolean persistEntries(@NotNull Map<K, D> entries) {
        boolean success = true;
        for (Map.Entry<K, D> entry : entries.entrySet()) {
            if (!this.persistEntry(entry.getKey(), entry.getValue())) {
                success = false;
            }
        }
        return success;
    }

    /**
     * Delete data under a given key.
     *
     * @param key The key
     * @return Whether the operation was successful
     */
    public abstract boolean deleteEntry(@NotNull K key);

    /**
     * Check if a key has any saved data.
     * <p>
     * Any exceptions generated by this method are
     * swallowed.
     *
     * @param key The key
     * @return Whether there is an entry for they key
     */
    public abstract boolean hasEntry(@NotNull K key);

    /**
     * Retrieve data for a given key.
     * <p>
     * Due to limitations of the caching layer,
     * this method must never return null.
     * <p>
     * Always use {@link AlpineDriver#hasEntry(Object)}
     * before attempting to retrieve an entry.
     * <p>
     * Any exceptions generated by this method are
     * NOT swallowed.
     *
     * @param key The key
     * @return The data associated with the key
     */
    @NotNull
    public abstract D retrieveEntry(@NotNull K key) throws Exception;

    /**
     * Retrieve all stored values in the data storage.
     * <p>
     * This method retrieves all values stored in the data storage and returns them as a collection.
     * It is a blocking task, and it may take some time to complete depending on the size of the data storage.
     * If the data storage is empty, an empty collection is returned.
     * <p>
     * Any exceptions generated by this method are NOT swallowed.
     *
     * @return A collection containing all stored values.
     * @throws Exception If an exception occurs while retrieving the values.
     */
    @NotNull
    public abstract Collection<D> getAllEntries() throws Exception;
}
