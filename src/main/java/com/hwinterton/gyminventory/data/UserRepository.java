/*
 * Purpose:
 * - database access for users tables
 * 
 * Function:
 * - returns user data and password hash for login verification
 * - creates new user row
 * - returns user summary for admin table
 * - supports protecting owner accounts
 * - supports password change
 * - sets new hash and forces password change
 * - updates user role
 * 
 * Dependencies:
 * - Database connection helper
 * - User, Role, UserSummary domain objects
 */

package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.domain.UserSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
	
	// Method - find user record by username for login verification
    public Optional<UserRecord> findByUsername(String username) {
        String sql = """
                SELECT id, username, password_hash, role, active, must_change_password
                FROM users
                WHERE lower(username) = lower(?)
                LIMIT 1;
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	
        	// set username parameter for case insensitive lookup
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) { 
            	// return empty if no matching user found
                if (!rs.next()) return Optional.empty();

                // read stored user fields from query result
                long id = rs.getLong("id");
                String uname = rs.getString("username");
                String hash = rs.getString("password_hash");
                Role role = Role.valueOf(rs.getString("role"));
                boolean active = rs.getInt("active") == 1;
                boolean mustChange = rs.getInt("must_change_password") == 1;

                // build domain user and pair with password hash
                User user = new User(id, uname, role, active, mustChange);
                return Optional.of(new UserRecord(user, hash));
            }

        } catch (Exception e) { // lookup failure
            throw new RuntimeException("Failed to query user", e);
        }
    }

    // Method - insert new user and return generated id
    public long insertUser(String username, String passwordHash, String role, boolean active, boolean mustChangePassword) {
        String sql = """
                INSERT INTO users(username, password_hash, role, active, must_change_password)
                VALUES(?, ?, ?, ?, ?);
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

        	// set values for new user row
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            ps.setInt(4, active ? 1 : 0);
            ps.setInt(5, mustChangePassword ? 1 : 0);

            // insert user record
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
            	// return generated primary key
                if (rs.next()) return rs.getLong(1);
            }
            // fail if insert succeeded but id was not returned
            throw new RuntimeException("User insert succeeded but no generated id returned.");

        } catch (Exception e) { // user insert failure
            throw new RuntimeException("Failed to insert user", e);
        }
    }
    
    // Method - return user summaries for admin view
    public List<UserSummary> listUsers() {
        String sql = """
                SELECT id, username, role, active, must_change_password
                FROM users
                ORDER BY username;
                """;

        List<UserSummary> users = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

        	// build summary object for each returned user
            while (rs.next()) {
                long id = rs.getLong("id");
                String username = rs.getString("username");
                Role role = Role.valueOf(rs.getString("role"));
                boolean active = rs.getInt("active") == 1;
                boolean mustChange = rs.getInt("must_change_password") == 1;

                users.add(new UserSummary(id, username, role, active, mustChange));
            }

            return users;

        } catch (Exception e) { // user list failure
            throw new RuntimeException("Failed to list users", e);
        }
    }

    // Method - return role for specific user id
    public Role getRoleById(long userId) {
        String sql = "SELECT role FROM users WHERE id = ?;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	
        	// set target user id
            ps.setLong(1, userId);

            // fail if user id does not exist
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("User not found.");
                return Role.valueOf(rs.getString("role"));
            }

        } catch (Exception e) { // role lookup failure
            throw new RuntimeException("Failed to read role", e);
        }
    }

    // Method - return password hash for specific user id
    public String getPasswordHashById(long userId) {
        String sql = "SELECT password_hash FROM users WHERE id = ?;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	// set target user id
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
            	// fail if user id does not exist
                if (!rs.next()) throw new RuntimeException("User not found.");
                return rs.getString("password_hash");
            }

        } catch (Exception e) { // password hash lookup failure
            throw new RuntimeException("Failed to read password hash", e);
        }
    }

    // Method - update user password after successful password change
    public void updatePassword(long userId, String newHash) {
        String sql = "UPDATE users SET password_hash = ?, must_change_password = 0 WHERE id = ?;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	// set new password hash and clear forced change flag
            ps.setString(1, newHash);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (Exception e) { // password update failure
            throw new RuntimeException("Failed to update password", e);
        }
    }

    // Method - reset password and require change on next login
    public void adminResetPassword(long userId, String newHash) {
        String sql = "UPDATE users SET password_hash = ?, must_change_password = 1 WHERE id = ?;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	// set new password hash and force password change
            ps.setString(1, newHash);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (Exception e) { // password reset failure
            throw new RuntimeException("Failed to admin reset password", e);
        }
    }

    // Method - update active flag for user account
    public void setActive(long userId, boolean active) {
        String sql = "UPDATE users SET active = ? WHERE id = ?;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	// store active flag as SQLite integer
            ps.setInt(1, active ? 1 : 0);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (Exception e) { // active flag update failure
            throw new RuntimeException("Failed to update active flag", e);
        }
    }

    // Method - update user role
    public void setRole(long userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE id = ?;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	// set new role value for user
            ps.setString(1, role);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (Exception e) { // role update failure
            throw new RuntimeException("Failed to update role", e);
        }
    }

    // Record - stores domain user plus password hash for login checks
    public record UserRecord(User user, String passwordHash) {}
}