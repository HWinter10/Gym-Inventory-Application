package com.hwinterton.gyminventory.data;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuditLogRepository {

    public void insert(Long userId, String action, String details) {
        String sql = "INSERT INTO audit_log(user_id, action, details) VALUES(?, ?, ?);";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (userId == null) {
                ps.setObject(1, null);
            } else {
                ps.setLong(1, userId);
            }
            ps.setString(2, action);
            ps.setString(3, details);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert audit log", e);
        }
    }
}