package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.UserRepository;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.PasswordUtil;

import java.util.Optional;

public class AuthenticationService {
    private final UserRepository userRepository = new UserRepository();

    public Optional<User> login(String username, String password) {
        if (username == null || username.isBlank()) return Optional.empty();
        if (password == null || password.isBlank()) return Optional.empty();

        return userRepository.findByUsername(username.trim())
                .filter(rec -> rec.user().isActive())
                .filter(rec -> PasswordUtil.verify(password, rec.passwordHash()))
                .map(UserRepository.UserRecord::user);
    }
}