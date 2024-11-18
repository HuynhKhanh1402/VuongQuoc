package dev.khanh.plugin.vuongquoc.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.khanh.plugin.kplugin.util.LoggerUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.database.dao.UserDAO;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class DatabaseManager {
    private final RealmPlugin plugin;
    private final HikariDataSource dataSource;
    private final UserDAO userDAO;

    public DatabaseManager(RealmPlugin plugin) {
        this.plugin = plugin;

        File dbFile = new File(plugin.getDataFolder(), "database.db");
        if (!dbFile.exists()) {
            try {
                if (dbFile.createNewFile()) {
                    LoggerUtil.info("Database file created: " + dbFile.getAbsolutePath());
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create database file: " + dbFile.getAbsolutePath(), e);
            }
        }

        LoggerUtil.info("Configuring HikariCP...");
        HikariConfig config = createHikariConfig(dbFile.getAbsolutePath());

        LoggerUtil.info("Connecting to the database...");
        this.dataSource = new HikariDataSource(config);
        LoggerUtil.info("Connected to the database successfully.");

        createTable();

        this.userDAO = new UserDAO(dataSource);
    }


    private HikariConfig createHikariConfig(String dbFilePath) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFilePath);
        config.setDriverClassName("org.sqlite.JDBC");

        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);

        return config;
    }


    private void createTable() {
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS USERS (
                    USERNAME VARCHAR(256) PRIMARY KEY,
                    REALM TEXT
                )
                """;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
            LoggerUtil.info("Table 'USERS' ensured.");
        } catch (SQLException e) {
            LoggerUtil.severe("Failed to create table 'USERS'.", e);
            throw new RuntimeException("Error while creating database table.", e);
        }
    }


    public void shutdown() {
        if (dataSource != null) {
            try {
                LoggerUtil.info("Shutting down database connection...");
                dataSource.close();
                LoggerUtil.info("Database connection shut down successfully.");
            } catch (Exception e) {
                LoggerUtil.severe("Error during database shutdown.", e);
            }
        }
    }
}
