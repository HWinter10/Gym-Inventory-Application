/*
 * Purpose:
 * - stores currently logged in user for running app session
 * 
 * Function:
 * - stores current User after login
 * - returns current User for controller and services
 * - clear removed user on logout
 * 
 * Dependencies:
 * - User domain object
 */

package com.hwinterton.gyminventory.security;

import com.hwinterton.gyminventory.domain.User;

public final class SessionManager {
    private static User currentUser; // currently authenticated user for session

    private SessionManager() {}

    // Method - store authenticated user after login
    public static void setUser(User user) {
        currentUser = user;
    }

    // Method - return current logged in user
    public static User getUser() {
    	
        // prevent access when session user not set
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in.");
        }
        return currentUser;
    }

    // Method - check if session currently has logged in user
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Method - clear session user on logout
    public static void clear() {
        currentUser = null;
    }
}