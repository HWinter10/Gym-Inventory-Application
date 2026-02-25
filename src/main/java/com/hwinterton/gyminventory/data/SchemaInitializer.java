package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.domain.FirstRunCredentials;
import com.hwinterton.gyminventory.security.PasswordUtil;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class SchemaInitializer {

    private SchemaInitializer() {}

    public static FirstRunCredentials initialize() {
        createTables();
        return seedOwnersIfEmpty();
    }

    private static void createTables() {
        String usersSql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL,
                    active INTEGER NOT NULL DEFAULT 1,
                    must_change_password INTEGER NOT NULL DEFAULT 0,
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

    private static FirstRunCredentials seedOwnersIfEmpty() {
        String countSql = "SELECT COUNT(*) AS c FROM users;";
        String insertSql = """
                INSERT INTO users(username, password_hash, role, active, must_change_password)
                VALUES(?, ?, ?, 1, 1);
                """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            int count = rs.next() ? rs.getInt("c") : 0;
            if (count > 0) return null;

            String ownerUsername = "owner";
            String backupUsername = "owner_backup";

            String ownerPassword = generateTempPassword(12);
            String backupPassword = generateTempPassword(12);

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, ownerUsername);
                ps.setString(2, PasswordUtil.hash(ownerPassword));
                ps.setString(3, "OWNER");
                ps.executeUpdate();

                ps.setString(1, backupUsername);
                ps.setString(2, PasswordUtil.hash(backupPassword));
                ps.setString(3, "OWNER");
                ps.executeUpdate();
            }

            System.out.println("FIRST RUN OWNER CREDENTIALS");
            System.out.println(ownerUsername + " / " + ownerPassword);
            System.out.println(backupUsername + " / " + backupPassword);

            return new FirstRunCredentials(
                    ownerUsername, ownerPassword,
                    backupUsername, backupPassword
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed owner accounts", e);
        }
    }

    private static String generateTempPassword(int length) {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}