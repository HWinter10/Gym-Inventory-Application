package com.hwinterton.gyminventory.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String DB_FOLDER = ".gyminventory";
    private static final String DB_FILE = "gym.db";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        try {
            Path dir = Path.of(System.getProperty("user.home"), DB_FOLDER);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String url = "jdbc:sqlite:" + dir.resolve(DB_FILE);
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to open database", e);
        }
    }
}