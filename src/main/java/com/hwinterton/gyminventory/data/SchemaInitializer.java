/*
 * Purpose:
 * - creates database tables and seeds initial required data
 * 
 * Function:
 * - creates tables if no existing, includes users and audit log
 * - ensures two owner accounts for recovery purposes
 * - sets must_change_password for seeded accounts
 * 
 * Dependencies:
 * - database connection helper
 * - SQL DDL and seed statements
 */

package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.security.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class SchemaInitializer {
	
	
    private SchemaInitializer() {}

    // Method - initialize database schema and seed required accounts
    public static void initialize() {
        createTables();
        ensureOwnerAccounts();
    }
    // Method - create required database tables
    private static void createTables() {
    	// users table: stores authentication and role information
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
        // audit_log table: records security and system actions
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
            conn.createStatement().execute(usersTable); // create users table if missing
            conn.createStatement().execute(auditTable); // create audit log table if missing
            
        } catch (Exception e) { // table creation error
            throw new RuntimeException("Failed to create tables", e);
        }
    }
    
    // Method - ensure default owner accounts exist
    private static void ensureOwnerAccounts() {
    	// primary owner account
        ensureUserExists("owner", "Owner!234", "OWNER", true, true);
        
        // backup owner account for recovery
        ensureUserExists("owner_backup", "OwnerBackup!234", "OWNER", true, true);
    }
    
    // Method - create user if username not already present
    private static void ensureUserExists(String username, String password, String role, boolean active, boolean mustChangePassword) {
    	// check existing user ignoring case
    	String checkSql = "SELECT id FROM users WHERE lower(username) = lower(?) LIMIT 1;";
        
    	// insert new user if not found
    	String insertSql = """
                INSERT INTO users(username, password_hash, role, active, must_change_password)
                VALUES(?, ?, ?, ?, ?);
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {

        	// set username parameter for lookup
            check.setString(1, username);

            try (ResultSet rs = check.executeQuery()) {
            	// stop if user already exists
                if (rs.next()) return;
            }

            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
            	// store username normalized to lowercase
                insert.setString(1, username.toLowerCase()); 
                
                // hash password before storing
                insert.setString(2, PasswordUtil.hash(password));
                
                // set role (OWNER / EMPLOYEE)
                insert.setString(3, role);
                
                // set active flag
                insert.setInt(4, active ? 1 : 0);
                
                // require password change on first login
                insert.setInt(5, mustChangePassword ? 1 : 0);
                
            	// insert user record
                insert.executeUpdate();
            }

        } catch (Exception e) { // user creation error
            throw new RuntimeException("Failed to ensure user exists: " + username, e);
        }
    }
}