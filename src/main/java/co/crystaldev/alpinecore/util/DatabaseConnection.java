package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.AlpineCore;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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

    private final HikariDataSource dataSource;

    public DatabaseConnection(@Nullable String table, @NotNull String url, @NotNull String uid, @NotNull String secret) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url + (table == null ? "" : table) + PARAMS);
        config.setUsername(uid);
        config.setPassword(secret);

        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(Duration.of(30L, ChronoUnit.SECONDS).toMillis());

        config.setMaxLifetime(Duration.ofMinutes(30).toMillis());
        config.setPoolName("AlpineCore-HikariCP-Pool");

        this.dataSource = new HikariDataSource(config);
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
