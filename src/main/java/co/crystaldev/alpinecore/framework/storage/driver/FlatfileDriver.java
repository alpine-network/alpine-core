package co.crystaldev.alpinecore.framework.storage.driver;

import co.crystaldev.alpinecore.Reference;
import co.crystaldev.alpinecore.framework.storage.KeySerializer;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * Implements a simple flatfile storage system where
 * each key is serialized into a separate JSON file
 * with the corresponding data as content.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class FlatfileDriver<K, D> extends AlpineDriver<K, D> {
    /** The directory the JSON files are stored in */
    private final File directory;

    /** The Gson instance responsible for serializing the data */
    private final Gson gson;

    /**
     * The data type of the value.
     * <p>
     * We need to feed this to Gson due to limitations on
     * its serialization of generics.
     */
    private final Class<D> dataType;

    /**
     * Locked down to ensure valid instantiation.
     *
     * @see Builder
     */
    private FlatfileDriver(File directory, Gson gson, Class<D> dataType) {
        this.directory = directory;
        this.gson = gson;
        this.dataType = dataType;
    }

    @Override
    public boolean persistEntry(@NotNull K key, @NotNull D data) {
        try {
            File file = this.getFileForKey(key);
            if (!file.exists() && !file.createNewFile())
                throw new IOException("Failed to create store file for " + key);

            FileWriter writer = new FileWriter(file);
            this.gson.toJson(data, writer);
            writer.close();
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteEntry(@NotNull K key) {
        try {
            File file = this.getFileForKey(key);
            return file.delete();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean hasEntry(@NotNull K key) {
        try {
            File file = this.getFileForKey(key);
            return file.exists();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public @NotNull D retrieveEntry(@NotNull K key) throws Exception {
        File file = this.getFileForKey(key);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return Reference.GSON_PRETTY.fromJson(reader, this.dataType);
    }

    @Override
    public @NotNull Collection<D> getAllEntries() throws Exception {
        File[] files = this.directory.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        // discover and deserialize values
        List<D> values = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            values.add(Reference.GSON_PRETTY.fromJson(reader, this.dataType));
        }

        // value should be immutable
        return ImmutableList.copyOf(values);
    }

    private File getFileForKey(K key) {
        KeySerializer<K, ?> serializer = null;
        for (Class<?> clazz : SERIALIZER_REGISTRY.getKeySerializers().keySet()) {
            if (clazz.isAssignableFrom(key.getClass())) {
                serializer = (KeySerializer<K, ?>) SERIALIZER_REGISTRY.getKeySerializer(clazz);
            }
        }

        if (serializer == null) {
            throw new NullPointerException(String.format("No key serializer registered for type \"%s\"", key.getClass().getName()));
        }

        Object serializedKey = serializer.serialize(key);
        String fileName = serializedKey.toString() + ".json";
        return new File(this.directory, fileName);
    }

    /**
     * Helper method to return a new builder instance.
     *
     * @see Builder
     * @return New builder for this class
     */
    public static <K, D> FlatfileDriver.Builder<K, D> builder() {
        return new Builder<>();
    }

    /**
     * Used to construct a new {@link FlatfileDriver}.
     *
     * @see co.crystaldev.alpinecore.framework.storage.AlpineStore
     */
    public static final class Builder<K, D> {
        private File directory;
        private Gson gson = Reference.GSON_PRETTY;
        private Class<D> dataType;

        /**
         * @see FlatfileDriver#directory
         */
        @NotNull @Contract("_ -> this")
        public Builder<K, D> directory(@NotNull File directory) {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            Validate.isTrue(directory.isDirectory(), "Must provide a valid directory");
            this.directory = directory;
            return this;
        }

        /**
         * @see FlatfileDriver#gson
         */
        @NotNull @Contract("_ -> this")
        public Builder<K, D> gson(@NotNull Gson gson) {
            this.gson = gson;
            return this;
        }

        /**
         * @see FlatfileDriver#dataType
         */
        @NotNull @Contract("_ -> this")
        public Builder<K, D> dataType(@NotNull Class<D> dataType) {
            this.dataType = dataType;
            return this;
        }

        /**
         * @return The newly constructed {@link FlatfileDriver}.
         */
        @NotNull
        public FlatfileDriver<K, D> build() {
            Validate.notNull(this.directory, "Directory must not be null");
            Validate.notNull(this.dataType, "Data type must not be null");
            return new FlatfileDriver<>(this.directory, this.gson, this.dataType);
        }
    }
}
