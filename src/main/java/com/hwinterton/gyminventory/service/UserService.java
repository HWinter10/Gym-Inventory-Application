/*
 * Purpose:
 * - business logic for user administration and password changes
 * 
 * Function:
 * owner actions:
 * - return summary list for UI
 * - creates user, hashes password, forces new password on first login
 * - updates role for non owner accounts
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
        var current = SessionManager.getUser(); // current session user
        AuthorizationService.require(AuthorizationService.canManageUsers(current)); // require owner permission
        return userRepository.listUsers();
    }

    // Method - create user with initial password and force password change
    public void createUserWithInitialPassword(String username, Role role, String initialPassword) {

        var current = SessionManager.getUser(); // current session user
        AuthorizationService.require(AuthorizationService.canManageUsers(current)); // require owner permission

        // validate username
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        // validate role
        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }

        // prevent owner creation through UI service
        if (role == Role.OWNER) {
            throw new IllegalArgumentException("Owner accounts are not created here.");
        }

        // normalize username for storage
        String uname = username.trim().toLowerCase();

        // validate password rules
        String p = initialPassword == null ? "" : initialPassword.trim();
        if (p.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        if (p.contains(" ")) {
            throw new IllegalArgumentException("Password cannot contain spaces.");
        }

        // hash password before storing
        String hash = PasswordUtil.hash(p);

        // insert user and return generated id
        long newUserId = userRepository.insertUser(uname, hash, role.name(), true, true);

        // record user creation event
        auditService.log(current.getId(), "CREATE_USER",
                "target_user_id=" + newUserId + " username=" + uname + " role=" + role.name() + " must_change_password=1");
    }

    // Method - enable or disable user account
    public void setActive(long targetUserId, boolean active) {

        var current = SessionManager.getUser(); // current session user
        AuthorizationService.require(AuthorizationService.canManageUsers(current)); // require owner permission

        // prevent disabling owner accounts
        if (isOwnerAccount(targetUserId)) {
            throw new IllegalArgumentException("Owner accounts cannot be disabled.");
        }

        userRepository.setActive(targetUserId, active);

        // record account status change
        auditService.log(current.getId(), "SET_USER_ACTIVE",
                "target_user_id=" + targetUserId + " active=" + active);
    }

    // Method - update user role
    public void changeRole(long targetUserId, Role newRole) {

        var current = SessionManager.getUser(); // current session user
        AuthorizationService.require(AuthorizationService.canManageUsers(current)); // require owner permission

        // validate role
        if (newRole == null) {
            throw new IllegalArgumentException("Role is required.");
        }

        // prevent assigning owner role through UI service
        if (newRole == Role.OWNER) {
            throw new IllegalArgumentException("Cannot assign Owner role here.");
        }

        // prevent role changes for owner accounts
        if (isOwnerAccount(targetUserId)) {
            throw new IllegalArgumentException("Owner accounts cannot be changed.");
        }

        userRepository.setRole(targetUserId, newRole.name());

        // record role change
        auditService.log(current.getId(), "CHANGE_USER_ROLE",
                "target_user_id=" + targetUserId + " role=" + newRole.name());
    }

    // Method - reset user password and require change at next login
    public void resetPasswordTo(long targetUserId, String newTempPassword) {

        var current = SessionManager.getUser(); // current session user
        AuthorizationService.require(AuthorizationService.canManageUsers(current)); // require owner permission

        // prevent resetting owner accounts
        if (isOwnerAccount(targetUserId)) {
            throw new IllegalArgumentException("Owner accounts cannot be reset here.");
        }

        // validate temporary password rules
        String p = newTempPassword == null ? "" : newTempPassword.trim();
        if (p.length() < 8) {
            throw new IllegalArgumentException("Temporary password must be at least 8 characters.");
        }
        if (p.contains(" ")) {
            throw new IllegalArgumentException("Temporary password cannot contain spaces.");
        }

        // hash new temporary password
        String hash = PasswordUtil.hash(p);

        userRepository.adminResetPassword(targetUserId, hash);

        // record password reset event
        auditService.log(current.getId(), "RESET_PASSWORD",
                "target_user_id=" + targetUserId + " must_change_password=1");
    }

    // Method - change password for currently logged in user
    public void changeOwnPassword(String currentPassword, String newPassword) {

        var user = SessionManager.getUser(); // current session user

        // read stored password hash
        String currentHash = userRepository.getPasswordHashById(user.getId());

        // verify current password
        if (!PasswordUtil.verify(currentPassword, currentHash)) {
            auditService.log(user.getId(), "CHANGE_PASSWORD_FAILED", "Bad current password");
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // validate new password rules
        String p = newPassword == null ? "" : newPassword.trim();
        if (p.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters.");
        }
        if (p.contains(" ")) {
            throw new IllegalArgumentException("New password cannot contain spaces.");
        }

        // hash new password and update stored value
        String newHash = PasswordUtil.hash(p);
        userRepository.updatePassword(user.getId(), newHash);

        // record successful password change
        auditService.log(user.getId(), "CHANGE_PASSWORD_SUCCESS", "Password changed");
    }

    // Method - check if target account has owner role
    private boolean isOwnerAccount(long userId) {
        return userRepository.getRoleById(userId) == Role.OWNER;
    }
}