package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UserRepository {

    public Optional<UserRecord> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role, active FROM users WHERE username = ? LIMIT 1;";
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

                return Optional.of(new UserRecord(
                        new User(id, uname, role, active),
                        hash
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to query user", e);
        }
    }

    public record UserRecord(User user, String passwordHash) {}
}