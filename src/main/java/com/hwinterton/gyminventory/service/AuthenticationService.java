package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.UserRepository;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.PasswordUtil;

import java.util.Optional;

public class AuthenticationService {
    private final UserRepository userRepository = new UserRepository();
    private final AuditService auditService = new AuditService();

    public Optional<User> login(String username, String password) {
    	String u = (username == null) ? "" : username.trim().toLowerCase();

        if (u.isBlank() || password == null || password.isBlank()) {
            auditService.log(null, "LOGIN_FAILED", "Blank username or password");
            return Optional.empty();
        }

        var recordOpt = userRepository.findByUsername(u);
        if (recordOpt.isEmpty()) {
            auditService.log(null, "LOGIN_FAILED", "Unknown username: " + u);
            return Optional.empty();
        }

        var record = recordOpt.get();
        if (!record.user().isActive()) {
            auditService.log(record.user().getId(), "LOGIN_FAILED", "Inactive account");
            return Optional.empty();
        }

        boolean ok = PasswordUtil.verify(password, record.passwordHash());
        if (!ok) {
            auditService.log(record.user().getId(), "LOGIN_FAILED", "Bad password");
            return Optional.empty();
        }

        auditService.log(record.user().getId(), "LOGIN_SUCCESS", "User logged in");
        return Optional.of(record.user());
    }
}