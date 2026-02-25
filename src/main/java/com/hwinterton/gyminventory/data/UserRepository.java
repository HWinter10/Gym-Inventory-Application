package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UserRepository {

    public Optional<UserRecord> findByUsername(String username) {
        String sql = """
                SELECT id, username, password_hash, role, active, must_change_password
                FROM users
                WHERE lower(username) = lower(?)
                LIMIT 1;
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                long id = rs.getLong("id");
                String uname = rs.getString("username");
                String hash = rs.getString("password_hash");
                Role role = Role.valueOf(rs.getString("role"));
                boolean active = rs.getInt("active") == 1;
                boolean mustChange = rs.getInt("must_change_password") == 1;

                return Optional.of(new UserRecord(
                        new User(id, uname, role, active, mustChange),
                        hash
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to query user", e);
        }
    }

    public long insertUser(String username, String passwordHash, String role, boolean active, boolean mustChangePassword) {
        String sql = """
                INSERT INTO users(username, password_hash, role, active, must_change_password)
                VALUES(?, ?, ?, ?, ?);
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            ps.setInt(4, active ? 1 : 0);
            ps.setInt(5, mustChangePassword ? 1 : 0);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new RuntimeException("User insert succeeded but no generated id returned.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to insert user", e);
        }
    }

    public String getPasswordHashById(long userId) {
        String sql = "SELECT password_hash FROM users WHERE id = ?;";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("User not found.");
                return rs.getString("password_hash");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read password hash", e);
        }
    }

    public void updatePassword(long userId, String newHash) {
        String sql = "UPDATE users SET password_hash = ?, must_change_password = 0 WHERE id = ?;";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to update password", e);
        }
    }

    public record UserRecord(User user, String passwordHash) {}
}