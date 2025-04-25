package de.joel.clansystem.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.joel.clansystem.ClanSystem;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

    private HikariDataSource dataSource;

    public boolean connect() {
        String host = ClanSystem.getInstance().getConfig().getString("MySQL.Host");
        int port = ClanSystem.getInstance().getConfig().getInt("MySQL.Port");
        String database = ClanSystem.getInstance().getConfig().getString("MySQL.Database");
        String username = ClanSystem.getInstance().getConfig().getString("MySQL.Username");
        String password = ClanSystem.getInstance().getConfig().getString("MySQL.Password");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);
            ClanSystem.getInstance().getLogger().info("✅ MySQL-Verbindung erfolgreich!");
            return true;
        } catch (Exception e) {
            ClanSystem.getInstance().getLogger().severe("❌ MySQL-Verbindung fehlgeschlagen: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
            ClanSystem.getInstance().getLogger().info("🔌 MySQL-Verbindung geschlossen.");
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            ClanSystem.getInstance().getLogger().warning("⚠ MySQL-Verbindung nicht verfügbar, versuche Reconnect...");
            if (!connect()) {
                throw new SQLException("MySQL-Verbindung kann nicht wiederhergestellt werden.");
            }
        }
        return dataSource.getConnection();
    }

    public void checkAndCreateTables() {
        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement()) {

            String createClansTable = "CREATE TABLE IF NOT EXISTS clans (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(32) NOT NULL UNIQUE, " +
                    "tag VARCHAR(5) NOT NULL UNIQUE" +
                    ")";
            stmt.executeUpdate(createClansTable);

            String createPlayersTable = "CREATE TABLE IF NOT EXISTS clan_members (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "clan_id INT, " +
                    "rank VARCHAR(16), " +
                    "PRIMARY KEY (uuid), " +
                    "FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE SET NULL" +
                    ")";
            stmt.executeUpdate(createPlayersTable);

            String createBannedClansTable = "CREATE TABLE IF NOT EXISTS banned_clans (" +
                    "word VARCHAR(32) NOT NULL UNIQUE" +
                    ")";
            stmt.executeUpdate(createBannedClansTable);

            ClanSystem.getInstance().getLogger().info("✅ MySQL-Tabellen überprüft und erstellt.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}