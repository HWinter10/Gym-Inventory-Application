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
	
    // // Method - insert new audit log row
    public void insert(Long userId, String action, String details) { 
        String sql = "INSERT INTO audit_log(actor_user_id, action, details) VALUES(?, ?, ?);";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // store null when no acting user is available
            if (userId == null) {
                ps.setObject(1, null);
            } else {
                ps.setLong(1, userId);
            }

            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();

        } catch (Exception e) { // audit log failure
            throw new RuntimeException("Failed to insert audit log", e);
        }
    }
}