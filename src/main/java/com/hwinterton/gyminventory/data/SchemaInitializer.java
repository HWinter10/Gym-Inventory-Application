package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.security.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class SchemaInitializer {

    private SchemaInitializer() {}

    public static void initialize() {
        createTables();
        seedAdminIfEmpty();
    }

    private static void createTables() {
        String usersSql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL,
                    active INTEGER NOT NULL DEFAULT 1,
                    created_at TEXT NOT NULL DEFAULT (datetime('now'))
                );
                """;

        String auditSql = """
                CREATE TABLE IF NOT EXISTS audit_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    action TEXT NOT NULL,
                    details TEXT,
                    created_at TEXT NOT NULL DEFAULT (datetime('now')),
                    FOREIGN KEY(user_id) REFERENCES users(id)
                );
                """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(usersSql);
            stmt.execute(auditSql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    private static void seedAdminIfEmpty() {
        String countSql = "SELECT COUNT(*) AS c FROM users;";
        String insertSql = "INSERT INTO users(username, password_hash, role, active) VALUES(?, ?, ?, 1);";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            int count = rs.next() ? rs.getInt("c") : 0;
            if (count > 0) return;

            String username = "admin";
            String defaultPassword = "admin123";
            String hash = PasswordUtil.hash(defaultPassword);

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, username);
                ps.setString(2, hash);
                ps.setString(3, "OWNER");
                ps.executeUpdate();
            }

            System.out.println("Seeded default admin user: admin / admin123");
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed admin user", e);
        }
    }
}