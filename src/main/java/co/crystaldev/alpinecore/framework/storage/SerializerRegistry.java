package co.crystaldev.alpinecore.framework.storage;

import de.exlll.configlib.Serializer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for managing serializers in the application.
 *
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/09/2023
 */
@Getter
public final class SerializerRegistry {

    // A map to store key serializers
    private final Map<Class<?>, KeySerializer<?, ?>> keySerializers = new HashMap<>();

    // A map to store config serializers
    private final Map<Class<?>, Serializer<?, ?>> configSerializers = new HashMap<>();

    /**
     * Registers a custom key serializer for a specific data type.
     *
     * @param dataType   The data type for which the serializer is registered.
     * @param serializer The custom key serializer.
     */
    public void putKeySerializer(@NotNull Class<?> dataType, @NotNull KeySerializer<?, ?> serializer) {
        this.keySerializers.put(dataType, serializer);
    }

    /**
     * Registers a custom config serializer for a specific data type.
     *
     * @param dataType   The data type for which the serializer is registered.
     * @param serializer The custom config serializer.
     */
    public void putConfigSerializer(@NotNull Class<?> dataType, @NotNull Serializer<?, ?> serializer) {
        this.configSerializers.put(dataType, serializer);
    }

    /**
     * Retrieves a key serializer for the specified data type.
     *
     * @param dataType The data type for which to retrieve the key serializer.
     * @return The key serializer associated with the data type, or null if not found.
     */
    @Nullable
    public KeySerializer<?, ?> getKeySerializer(@NotNull Class<?> dataType) {
        return this.keySerializers.get(dataType);
    }
}
