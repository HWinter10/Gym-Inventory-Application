package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.UserRepository;
import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.security.AuthorizationService;
import com.hwinterton.gyminventory.security.PasswordUtil;
import com.hwinterton.gyminventory.security.SessionManager;

import java.security.SecureRandom;

public class UserService {

    private final UserRepository userRepository = new UserRepository();
    private final AuditService auditService = new AuditService();

    public String createUserWithTempPassword(String username, Role role) {
        var current = SessionManager.getUser();
        AuthorizationService.require(AuthorizationService.canManageUsers(current));

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }
        if (role == Role.OWNER) {
            throw new IllegalArgumentException("Owner accounts are not created here.");
        }

        String uname = username.trim().toLowerCase();
        String tempPassword = generateTempPassword(12);
        String hash = PasswordUtil.hash(tempPassword);

        long newUserId = userRepository.insertUser(uname, hash, role.name(), true, true);

        auditService.log(current.getId(), "CREATE_USER",
                "Created user_id=" + newUserId + " username=" + uname + " role=" + role.name());

        return tempPassword;
    }

    public void changeOwnPassword(String currentPassword, String newPassword) {
        var user = SessionManager.getUser();

        String currentHash = userRepository.getPasswordHashById(user.getId());
        if (!PasswordUtil.verify(currentPassword, currentHash)) {
            auditService.log(user.getId(), "CHANGE_PASSWORD_FAILED", "Bad current password");
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters.");
        }

        String newHash = PasswordUtil.hash(newPassword);
        userRepository.updatePassword(user.getId(), newHash);

        auditService.log(user.getId(), "CHANGE_PASSWORD_SUCCESS", "Password changed");
    }

    private static String generateTempPassword(int length) {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}