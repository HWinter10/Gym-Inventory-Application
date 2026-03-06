/*
 * Purpose:
 * - role based authorization checks
 * 
 * Function:
 * - provides helper checks
 * - throws exception for actions not allowed
 * 
 * Dependencies:
 * - User and Role domains
 */

package com.hwinterton.gyminventory.security;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;

public final class AuthorizationService {


    private AuthorizationService() {}

    // Method - check permission to manage users
    public static boolean canManageUsers(User user) {
        // only owner role allowed to manage users
        return user.getRole() == Role.OWNER;
    }

    // Method - check permission to manage products
    public static boolean canManageProducts(User user) {
        // owners and managers allowed product management access
        return user.getRole() == Role.OWNER || user.getRole() == Role.MANAGER;
    }

    // Method - enforce authorization requirement
    public static void require(boolean condition) {
        if (!condition) throw new SecurityException("Not authorized for this action."); // permission check failure
    }
}