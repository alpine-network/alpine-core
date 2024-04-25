package co.crystaldev.alpinecore.framework.storage.driver;

import co.crystaldev.alpinecore.AlpineCore;
import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.Reference;
import co.crystaldev.alpinecore.framework.storage.KeySerializer;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import co.crystaldev.alpinecore.util.DatabaseConnection;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author BestBearr
 * @since 0.1.2
 */
@ApiStatus.Experimental
public class MySqlDriver<K, D> extends AlpineDriver<K, D> {

    private final Class<D> dataType;
    private final Gson gson;

    private final DatabaseConnection connection;
    private final String table;

    private MySqlDriver(@NotNull AlpinePlugin plugin, @NotNull String url, @NotNull String table, @NotNull String username,
                        @NotNull String password, @NotNull Class<D> dataType, @NotNull Gson gson) {
        super(plugin);
        this.table = table;
        this.dataType = dataType;
        this.gson = gson;

        // Setup connection
        this.connection = new DatabaseConnection(null, url, username, password);

        // Establish a connection to the database
        if (this.getConnection() == null) {
            throw new IllegalStateException("Unable to establish connection to the database");
        }

        // Ensure the table exists in the database
        try {
            if (!this.doesTableExist()) {
                this.createTable();
            }
        }
        catch (SQLException ex) {
            throw new IllegalStateException(String.format("Unable to create table \"%s\"", this.table), ex);
        }

        // Ensure all required columns are present
        try {
            if (!this.validateColumns()) {
                throw new IllegalStateException("Table columns do not match");
            }
        }
        catch (SQLException ex) {
            throw new IllegalStateException(String.format("Unable to validate columns in table \"%s\"", this.table), ex);
        }
    }

    @Override
    public boolean persistEntry(@NotNull K key, @NotNull D data) {
        Connection conn = this.getConnection();
        String sql = "INSERT INTO " + this.table + " (data_key, storage) VALUES (?, ?) ON DUPLICATE KEY UPDATE storage = ?";

        if (conn == null) {
            throw new IllegalStateException("Database connection is not active");
        }

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setObject(1, this.serializeKey(key));
            statement.setString(2, this.gson.toJson(data));

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
        catch (SQLException ex) {
            AlpineCore.getInstance().log("Unable to persist entry", ex);
            return false;
        }
    }

    @Override
    public boolean persistEntries(@NotNull Map<K, D> entries) {
        Connection conn = this.getConnection();
        String sql = "INSERT INTO " + this.table + " (data_key, storage) VALUES (?, ?) ON DUPLICATE KEY UPDATE storage = VALUES(storage)";

        if (conn == null) {
            throw new IllegalStateException("Database connection is not active");
        }

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (Map.Entry<K, D> entry : entries.entrySet()) {
                K key = entry.getKey();
                D value = entry.getValue();

                statement.setObject(1, this.serializeKey(key));
                statement.setString(2, this.gson.toJson(value));
                statement.addBatch();
            }

            statement.executeBatch();
            conn.commit();

            return true;
        }
        catch (SQLException ex) {
            try {
                conn.rollback();
            }
            catch (SQLException ignored) {
                // NO-OP
            }
            ex.printStackTrace();
            return false;
        }
        finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            }
            catch (SQLException ignored) {
                // NO-OP
            }
        }
    }

    @Override
    public boolean deleteEntry(@NotNull K key) {
        Connection conn = this.getConnection();
        String sql = "DELETE FROM " + this.table + " WHERE data_key = ?";

        if (conn == null) {
            throw new IllegalStateException("Database connection is not active");
        }

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setObject(1, this.serializeKey(key));

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
        catch (SQLException ex) {
            AlpineCore.getInstance().log("Unable to delete entry", ex);
            return false;
        }
        finally {
            try {
                conn.close();
            }
            catch (SQLException e) {
                // NO-OP
            }
        }
    }

    @Override
    public boolean hasEntry(@NotNull K key) {
        Connection conn = this.getConnection();
        String sql = "SELECT COUNT(*) FROM " + this.table + " WHERE data_key = ?";

        if (conn == null) {
            throw new IllegalStateException("Database connection is not active");
        }

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setObject(1, this.serializeKey(key));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

            return false;
        }
        catch (SQLException ex) {
            AlpineCore.getInstance().log("Unable to fetch entry", ex);
            return false;
        }
        finally {
            try {
                conn.close();
            }
            catch (SQLException e) {
                // NO-OP
            }
        }
    }

    @Override
    public @NotNull D retrieveEntry(@NotNull K key) throws Exception {
        Connection conn = this.getConnection();
        String sql = "SELECT storage FROM " + this.table + " WHERE data_key = ?";

        if (conn == null) {
            throw new IllegalStateException("Database connection is not active");
        }

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setObject(1, this.serializeKey(key));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String data = resultSet.getString("storage");
                    return this.gson.fromJson(data, this.dataType);
                }
            }
        }
        finally {
            try {
                conn.close();
            }
            catch (SQLException e) {
                // NO-OP
            }
        }

        throw new NoSuchElementException(String.format("No entry found for key \"%s\"", this.serializeKey(key)));
    }

    @Override
    public @NotNull Collection<D> getAllEntries() throws Exception {
        Connection conn = this.getConnection();

        if (conn == null) {
            throw new IllegalStateException("Database connection is not active");
        }

        String sql = "SELECT storage FROM " + this.table;
        List<D> entries = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String data = resultSet.getString("storage");
                entries.add(this.gson.fromJson(data, this.dataType));
            }
        }
        finally {
            try {
                conn.close();
            }
            catch (SQLException e) {
                // NO-OP
            }
        }

        return ImmutableList.copyOf(entries);
    }

    @Override
    public @NotNull Collection<D> getAllEntries(@Nullable Consumer<Exception> exceptionConsumer) {
        Connection conn = this.getConnection();

        if (conn == null) {
            throw new IllegalStateException("Database connection is not active");
        }

        String sql = "SELECT storage FROM " + this.table;
        List<D> entries = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                try {
                    String data = resultSet.getString("storage");
                    entries.add(this.gson.fromJson(data, this.dataType));
                }
                catch (Exception ex) {
                    if (exceptionConsumer != null) {
                        exceptionConsumer.accept(ex);
                    }
                }
            }
        }
        catch (SQLException ex) {
            if (exceptionConsumer != null) {
                exceptionConsumer.accept(ex);
            }
        }
        finally {
            try {
                conn.close();
            }
            catch (SQLException e) {
                // NO-OP
            }
        }

        return ImmutableList.copyOf(entries);
    }

    @Override
    public void shutdown() {
        this.connection.shutdown();
    }

    @NotNull
    private Object serializeKey(@NotNull K key) {
        SerializerRegistry registry = this.plugin.getSerializerRegistry();
        KeySerializer<K, ?> serializer = null;
        for (Class<?> clazz : registry.getKeySerializers().keySet()) {
            if (clazz.isAssignableFrom(key.getClass())) {
                serializer = (KeySerializer<K, ?>) registry.getKeySerializer(clazz);
            }
        }

        if (serializer == null) {
            throw new NullPointerException(String.format("No key serializer registered for type \"%s\"", key.getClass().getName()));
        }

        return serializer.serialize(key);
    }

    @Nullable
    private Connection getConnection() {
        try {
            return this.connection.getConnection();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean doesTableExist() throws SQLException {
        try (Connection conn = this.connection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, this.table, null)) {
                return rs.next();
            }
        }
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE " + this.table + " (id INT AUTO_INCREMENT PRIMARY KEY, data_key VARCHAR(255) NOT NULL, storage JSON, UNIQUE(data_key))";
        try (Connection conn = this.connection.getConnection();
             Statement statement = conn.createStatement()) {
            statement.execute(sql);
        }
    }

    private boolean validateColumns() throws SQLException {
        String[] columns = { "id", "data_key", "storage" };
        List<String> existingColumns = new ArrayList<>();

        try (Connection conn = this.connection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, this.table, null)) {
                while (rs.next()) {
                    existingColumns.add(rs.getString("COLUMN_NAME"));
                }
            }
        }

        for (String column : columns) {
            if (!existingColumns.contains(column))
                return false;
        }
        return true;
    }

    /**
     * Helper method to return a new builder instance.
     *
     * @see Builder
     * @return New builder for this class
     */
    @NotNull
    public static <K, D> Builder<K, D> builder() {
        return new Builder<>();
    }

    /**
     * Used to construct a new {@link MySqlDriver}.
     *
     * @see co.crystaldev.alpinecore.framework.storage.AlpineStore
     */
    public static final class Builder<K, D> {

        private String url;
        private String table;
        private String username;
        private String password;

        private Class<D> dataType;

        private Gson gson = Reference.GSON;

        @NotNull
        public Builder<K, D> url(@NotNull String url) {
            this.url = url;
            return this;
        }

        @NotNull
        public Builder<K, D> host(@NotNull String host, int port, @NotNull String database, @NotNull String table) {
            this.table = table;
            if (port < 0) {
                return this.url(String.format("jdbc:mysql://%s/%s", host, database));
            }
            else {
                return this.url(String.format("jdbc:mysql://%s:%s/%s", host, port, database));
            }
        }

        @NotNull
        public Builder<K, D> host(@NotNull String host, @NotNull String database, @NotNull String table) {
            return this.host(host, -1, database, table);
        }

        @NotNull
        public Builder<K, D> credentials(@NotNull String username, @NotNull String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        @NotNull
        public Builder<K, D> dataType(@NotNull Class<D> dataType) {
            this.dataType = dataType;
            return this;
        }

        @NotNull
        public Builder<K, D> gson(@NotNull Gson gson) {
            this.gson = gson;
            return this;
        }

        @NotNull
        public MySqlDriver<K, D> build(@NotNull AlpinePlugin plugin) {
            Validate.notNull(this.url, "url must not be null");
            Validate.notNull(this.table, "table must not be null");
            Validate.notNull(this.dataType, "dataType must not be null");
            return new MySqlDriver<>(plugin, this.url, this.table, this.username, this.password, this.dataType, this.gson);
        }

        @NotNull
        @Deprecated
        public MySqlDriver<K, D> build() {
            return this.build(AlpineCore.getInstance());
        }
    }
}
