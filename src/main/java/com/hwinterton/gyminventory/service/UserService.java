/*
 * Purpose:
 * - business logic for user administration and password changes
 * 
 * Function:
 * owner actions:
 * - return summary list for UI
 * - creates user, hashes password, forces new password on first login
 * - updates username and role for non owner accounts
 * - disables or enables non owner accounts
 * - resets passwords and forces change next login
 * 
 * self service actions:
 * - verifies current password, updates hash, clears mustChangePassword flag
 * 
 * security rules:
 * - owner account cannot be disabled, reset, or undergo role changes
 * - role cannot be set to owner through UI service methods
 * 
 * Dependencies:
 * - UserRepository for database updates
 * - AuthorizationService and SessionManager for permission checks
 * - AuditService for logging sensitive actions
 */

package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.UserRepository;
import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.UserSummary;
import com.hwinterton.gyminventory.security.AuthorizationService;
import com.hwinterton.gyminventory.security.PasswordUtil;
import com.hwinterton.gyminventory.security.SessionManager;

import java.util.List;

public class UserService {

    private final UserRepository userRepository = new UserRepository(); // user persistence access
    private final AuditService auditService = new AuditService(); // security audit logging

    // Method - return user summaries for admin UI
    public List<UserSummary> listUsers() {
        var current = SessionManager.getUser();
        AuthorizationService.require(AuthorizationService.canManageUsers(current));
        return userRepository.listUsers();
    }

    // Method - create user with initial password and force password change
    public void createUserWithInitialPassword(String username, Role role, String initialPassword) {

        var current = SessionManager.getUser();
        AuthorizationService.require(AuthorizationService.canManageUsers(current));

        String uname = validateUsername(username);

        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }

        if (role == Role.OWNER) {
            throw new IllegalArgumentException("Owner accounts are not created here.");
        }

        String p = initialPassword == null ? "" : initialPassword.trim();
        if (p.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        if (p.contains(" ")) {
            throw new IllegalArgumentException("Password cannot contain spaces.");
        }

        String hash = PasswordUtil.hash(p);

        long newUserId = userRepository.insertUser(uname, hash, role.name(), true, true);

        auditService.log(current.getId(), "CREATE_USER",
                "target_user_id=" + newUserId + " username=" + uname + " role=" + role.name() + " must_change_password=1");
    }

    // Method - update selected user username and role
    public void updateUser(long targetUserId, String username, Role newRole) {

        var current = SessionManager.getUser();
        AuthorizationService.require(AuthorizationService.canManageUsers(current));

        if (targetUserId <= 0) {
            throw new IllegalArgumentException("Valid user id is required.");
        }

        String uname = validateUsername(username);

        if (newRole == null) {
            throw new IllegalArgumentException("Role is required.");
        }

        if (newRole == Role.OWNER) {
            throw new IllegalArgumentException("Cannot assign Owner role here.");
        }

        if (isOwnerAccount(targetUserId)) {
            throw new IllegalArgumentException("Owner accounts cannot be changed.");
        }

        userRepository.setUsername(targetUserId, uname);
        userRepository.setRole(targetUserId, newRole.name());

        auditService.log(current.getId(), "UPDATE_USER",
                "target_user_id=" + targetUserId + " username=" + uname + " role=" + newRole.name());
    }

    // Method - enable or disable user account
    public void setActive(long targetUserId, boolean active) {

        var current = SessionManager.getUser();
        AuthorizationService.require(AuthorizationService.canManageUsers(current));

        if (isOwnerAccount(targetUserId)) {
            throw new IllegalArgumentException("Owner accounts cannot be disabled.");
        }

        userRepository.setActive(targetUserId, active);

        auditService.log(current.getId(), "SET_USER_ACTIVE",
                "target_user_id=" + targetUserId + " active=" + active);
    }

    // Method - reset user password and require change at next login
    public void resetPasswordTo(long targetUserId, String newTempPassword) {

        var current = SessionManager.getUser();
        AuthorizationService.require(AuthorizationService.canManageUsers(current));

        if (isOwnerAccount(targetUserId)) {
            throw new IllegalArgumentException("Owner accounts cannot be reset here.");
        }

        String p = newTempPassword == null ? "" : newTempPassword.trim();
        if (p.length() < 8) {
            throw new IllegalArgumentException("Temporary password must be at least 8 characters.");
        }
        if (p.contains(" ")) {
            throw new IllegalArgumentException("Temporary password cannot contain spaces.");
        }

        String hash = PasswordUtil.hash(p);

        userRepository.adminResetPassword(targetUserId, hash);

        auditService.log(current.getId(), "RESET_PASSWORD",
                "target_user_id=" + targetUserId + " must_change_password=1");
    }

    // Method - change password for currently logged in user
    public void changeOwnPassword(String currentPassword, String newPassword) {

        var user = SessionManager.getUser();

        String currentHash = userRepository.getPasswordHashById(user.getId());

        if (!PasswordUtil.verify(currentPassword, currentHash)) {
            auditService.log(user.getId(), "CHANGE_PASSWORD_FAILED", "Bad current password");
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        String p = newPassword == null ? "" : newPassword.trim();
        if (p.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters.");
        }
        if (p.contains(" ")) {
            throw new IllegalArgumentException("New password cannot contain spaces.");
        }

        String newHash = PasswordUtil.hash(p);
        userRepository.updatePassword(user.getId(), newHash);

        auditService.log(user.getId(), "CHANGE_PASSWORD_SUCCESS", "Password changed");
    }

    // Method - validate and normalize username
    private String validateUsername(String username) {
        String uname = username == null ? "" : username.trim().toLowerCase();

        if (uname.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (uname.contains(" ")) {
            throw new IllegalArgumentException("Username cannot contain spaces.");
        }

        return uname;
    }

    // Method - check if target account has owner role
    private boolean isOwnerAccount(long userId) {
        return userRepository.getRoleById(userId) == Role.OWNER;
    }
}