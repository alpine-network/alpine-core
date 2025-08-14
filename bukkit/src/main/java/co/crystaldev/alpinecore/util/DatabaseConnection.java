package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.AlpineCore;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

/**
 * @author BestBearr
 * @since 0.3.5
 */
public final class DatabaseConnection {

    private static final String PARAMS = String.join("&",
            "?useJDBCCompliantTimezoneShift=true",
            "serverTimezone=UTC",
            "useUnicode=true"
    );

    private final BasicDataSource dataSource;

    public DatabaseConnection(@Nullable String table, @NotNull String url, @NotNull String uid, @NotNull String secret) {
        this.dataSource = new BasicDataSource();
        this.dataSource.setUrl(url + (table == null ? "" : table) + PARAMS);
        this.dataSource.setUsername(uid);
        this.dataSource.setPassword(secret);

        this.dataSource.setMinIdle(5);
        this.dataSource.setMaxIdle(10);
        this.dataSource.setMaxWait(Duration.of(30L, ChronoUnit.SECONDS));

        this.dataSource.setMaxOpenPreparedStatements(100);
        this.dataSource.setPoolPreparedStatements(true);
    }

    public @NotNull Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public void shutdown() {
        try {
            this.dataSource.close();
        }
        catch (Exception ex) {
            AlpineCore.getInstance().getLogger().log(Level.SEVERE, "Unable to close data source", ex);
        }
    }
}
