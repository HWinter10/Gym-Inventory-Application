package com.hwinterton.gyminventory.security;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;

public final class AuthorizationService {

    private AuthorizationService() {}

    public static boolean canManageUsers(User user) {
        return user.getRole() == Role.OWNER;
    }

    public static boolean canManageProducts(User user) {
        return user.getRole() == Role.OWNER || user.getRole() == Role.MANAGER;
    }

    public static void require(boolean condition) {
        if (!condition) throw new SecurityException("Not authorized for this action.");
    }
}