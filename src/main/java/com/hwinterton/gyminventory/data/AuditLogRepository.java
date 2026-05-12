package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.domain.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides database access for the audit_log table.
 *
 * <p>This repository centralizes audit log SQL so the rest of the application
 * does not need to know the database details.</p>
 *
 * <p>Uses {@link Database} to open SQLite connections and {@link AuditLog} to
 * represent audit log rows loaded from the database.</p>
 */
public class AuditLogRepository {
	
	/**
     * Inserts a new audit log row.
     *
     * @param userId the user id connected to the action, or null if unavailable
     * @param action the action being recorded
     * @param details extra information about the action
     */
    public void insert(Long userId, String action, String details) { 
        String sql = "INSERT INTO audit_log(actor_user_id, action, details) VALUES(?, ?, ?);";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // handle optional actor user id
            if (userId == null) {
                ps.setObject(1, null);
            } else {
                ps.setLong(1, userId);
            }
            // set audit log values
            ps.setString(2, action);
            ps.setString(3, details);
            // insert row
            ps.executeUpdate();

        } catch (Exception e) { // audit log failure
            throw new RuntimeException("Failed to insert audit log", e);
        }
    }
    
    /**
     * Finds recent audit log records.
     *
     * <p>Records are returned newest first. The users table is joined so the viewer
     * can show a username when one is available.</p>
     *
     * @param limit the maximum number of rows to load
     * @return recent audit log records ordered newest first
     */
    public List<AuditLog> findRecent(int limit) {
        String sql = """
                SELECT
                    audit_log.id,
                    audit_log.created_at,
                    audit_log.actor_user_id,
                    users.username,
                    audit_log.action,
                    audit_log.details
                FROM audit_log
                LEFT JOIN users ON audit_log.actor_user_id = users.id
                ORDER BY audit_log.created_at DESC, audit_log.id DESC
                LIMIT ?;
                """;

        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // limit how many rows the screen loads
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {

                // read each returned row
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String createdAt = rs.getString("created_at");

                    Long actorUserId = null;
                    long rawActorUserId = rs.getLong("actor_user_id");

                    // SQLite null handling for optional actor_user_id
                    if (!rs.wasNull()) {
                        actorUserId = rawActorUserId;
                    }

                    String username = rs.getString("username");
                    String action = rs.getString("action");
                    String details = rs.getString("details");

                    // map database row to domain object
                    logs.add(new AuditLog(id, createdAt, actorUserId, username, action, details));
                }
            }

            return logs;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load audit logs", e);
        }
    }
}