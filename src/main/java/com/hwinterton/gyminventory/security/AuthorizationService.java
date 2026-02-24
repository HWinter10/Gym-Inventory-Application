package com.hwinterton.gyminventory.security;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;

public final class AuthorizationService {

    private AuthorizationService() {}

    public static void requireAnyRole(User user, Role... allowed) {
        for (Role role : allowed) {
            if (user.getRole() == role) return;
        }
        throw new SecurityException("Not authorized for this action.");
    }

    public static void requireLogin() {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("Not logged in.");
        }
    }
}