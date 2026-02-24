package com.hwinterton.gyminventory.security;

import com.hwinterton.gyminventory.domain.User;

public final class SessionManager {
    private static User currentUser;

    private SessionManager() {}

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in.");
        }
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
}