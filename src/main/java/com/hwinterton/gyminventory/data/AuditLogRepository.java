/*
 * Purpose:
 * - database access for the audit_log table
 * 
 * Function:
 * - insert audit log rows with timestamps, user id, action, and details
 * - provide single place for audit log SQL
 * 
 * Dependencies:
 * - Database connection helper
 */

package com.hwinterton.gyminventory.data;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuditLogRepository {
	
	// Method - insert new audit log
	// records action, which user performed it, and additional details
    public void insert(Long userId, String action, String details) { 
    	
    	// SQL statement - creates new audit log record
        String sql = "INSERT INTO audit_log(user_id, action, details) VALUES(?, ?, ?);";
        
        // try-with-resources - automatically closes connection and statement
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	// if action not associated with user, store NULL
            if (userId == null) {
                ps.setObject(1, null);
            } else {
                ps.setLong(1, userId); // set userID
            }
            ps.setString(2, action); // set action performed (ex: LOGIN_SUCCESS, INVENTORY_ADJUSTMENT)
            ps.setString(3, details); // set details for event
            
            ps.executeUpdate(); // insert operation
            
        } catch (Exception e) { // database error
            throw new RuntimeException("Failed to insert audit log", e);
        }
    }
}