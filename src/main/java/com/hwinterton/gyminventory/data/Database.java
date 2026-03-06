/*
 * Purpose:
 * - single way to open SQLite connections
 * 
 * Function:
 * - builds database file path
 * - returns connection
 * - keeps connection creation consistent across repositories
 * 
 * Dependencies:
 * - SQLite JDBC driver
 */

package com.hwinterton.gyminventory.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
	
    private static final String DB_FOLDER = ".gyminventory"; // database storage in user home directory
    private static final String DB_FILE = "gym.db"; // SQLite database file name
    
    // Method - open database connection
    private Database() {}
    public static Connection getConnection() throws SQLException {
        try {
        	// build path: userHome/.gyminventory
            Path dir = Path.of(System.getProperty("user.home"), DB_FOLDER);
            
            // create if missing
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            
            // build SQLite JDBC connection URL
            String url = "jdbc:sqlite:" + dir.resolve(DB_FILE);
            
            // open connection to SQLite database file
            return DriverManager.getConnection(url);
            
        } catch (Exception e) { // database error
            throw new RuntimeException("Failed to open database", e);
        }
    }
}