/*
 * Purpose:
 * - creates database tables and seeds initial required data
 * 
 * Function:
 * - creates required tables if missing
 * - detects first run by checking whether users already exist
 * - generates temporary passwords for owner and backup owner accounts
 * - stores hashed passwords only
 * - records first run credentials in StartupContext for display
 * 
 * Dependencies:
 * - Database connection helper
 * - PasswordUtil for password hashing
 * - CredentialGenerator for temporary password generation
 * - StartupContext for first run credential display
 */

package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.security.PasswordUtil;
import com.hwinterton.gyminventory.startup.CredentialGenerator;
import com.hwinterton.gyminventory.startup.StartupContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class SchemaInitializer {

    private SchemaInitializer() {}

    // Method - initialize database schema and first run seed data
    public static void initialize() {
        createTables();

        // only seed accounts if no users exist yet
        if (!hasAnyUsers()) {
            seedInitialOwnerAccounts();
        }
    }

    // Method - create required database tables
    private static void createTables() {
        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL,
                    active INTEGER NOT NULL DEFAULT 1,
                    must_change_password INTEGER NOT NULL DEFAULT 0
                );
                """;

        String auditTable = """
                CREATE TABLE IF NOT EXISTS audit_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    created_at TEXT NOT NULL DEFAULT (datetime('now')),
                    actor_user_id INTEGER,
                    action TEXT NOT NULL,
                    details TEXT
                );
                """;

        try (Connection conn = Database.getConnection()) {
            conn.createStatement().execute(usersTable);
            conn.createStatement().execute(auditTable);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    // Method - return true if any user already exists
    private static boolean hasAnyUsers() {
        String sql = "SELECT COUNT(*) FROM users;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (Exception e) {
            throw new RuntimeException("Failed to check existing users", e);
        }
    }

    // Method - create first run owner accounts with generated temporary passwords
    private static void seedInitialOwnerAccounts() {
        String ownerPassword = CredentialGenerator.generatePassword();
        String backupPassword = CredentialGenerator.generatePassword();

        insertInitialUser("owner", ownerPassword, "OWNER", true, true);
        insertInitialUser("owner_backup", backupPassword, "OWNER", true, true);

        StartupContext.setFirstRunCredentials(ownerPassword, backupPassword);
    }

    // Method - insert initial user with hashed password
    private static void insertInitialUser(String username, String plainPassword, String role, boolean active, boolean mustChangePassword) {
        String insertSql = """
                INSERT INTO users(username, password_hash, role, active, must_change_password)
                VALUES(?, ?, ?, ?, ?);
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement insert = conn.prepareStatement(insertSql)) {

            insert.setString(1, username.toLowerCase());
            insert.setString(2, PasswordUtil.hash(plainPassword));
            insert.setString(3, role);
            insert.setInt(4, active ? 1 : 0);
            insert.setInt(5, mustChangePassword ? 1 : 0);

            insert.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to insert initial user: " + username, e);
        }
    }
}