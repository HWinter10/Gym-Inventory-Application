/*
 * Purpose:
 * - validates login attempts and returns User if credentials correct
 * 
 * Function:
 * - normalizes username
 * - looks up user record by username using UserRepository
 * - rejects inactive accounts
 * - verifies password with PasswordUtil against stored hash
 * - writes audit logs for success and failure
 * 
 * Dependencies:
 * - UserRepository for user lookup
 * - PasswordUtil for hash verification
 * - AuditService for audit logging
 */

package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.UserRepository;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.PasswordUtil;

import java.util.Optional;

public class AuthenticationService {

    private final UserRepository userRepository = new UserRepository(); // user lookup access
    private final AuditService auditService = new AuditService(); // security event logging

    // Method - validate login credentials and return authenticated user
    public Optional<User> login(String username, String password) {

        // normalize username for consistent lookup
        String u = (username == null) ? "" : username.trim().toLowerCase();

        // reject blank credentials
        if (u.isBlank() || password == null || password.isBlank()) {
            auditService.log(null, "LOGIN_FAILED", "Blank username or password");
            return Optional.empty();
        }

        // lookup user record by username
        var recordOpt = userRepository.findByUsername(u);

        // fail if username not found
        if (recordOpt.isEmpty()) {
            auditService.log(null, "LOGIN_FAILED", "Invalid credentials");
            return Optional.empty();
        }

        var record = recordOpt.get();

        // reject inactive accounts
        if (!record.user().isActive()) {
            auditService.log(record.user().getId(), "LOGIN_FAILED", "Inactive account");
            return Optional.empty();
        }

        // verify password against stored BCrypt hash
        boolean ok = PasswordUtil.verify(password, record.passwordHash());

        if (!ok) {
            auditService.log(record.user().getId(), "LOGIN_FAILED", "Invalid credentials");
            return Optional.empty();
        }

        // record successful login
        auditService.log(record.user().getId(), "LOGIN_SUCCESS", "User logged in");

        return Optional.of(record.user());
    }
}