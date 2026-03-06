/*
 * Purpose:
 * - password hashing and verification utility
 * 
 * Function:
 * - hash takes plain text and returns BCrypt hash
 * - compares plain text to stored BCrypt hash
 * 
 * Dependencies:
 * - BCrypt library
 */

package com.hwinterton.gyminventory.security;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {}

    // Method - hash plain text password using BCrypt
    public static String hash(String plainPassword) {
        // generate salted BCrypt hash with work factor 12
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Method - verify plain password against stored hash
    public static boolean verify(String plainPassword, String hashed) {
    	
        // prevent verification when input values missing
        if (plainPassword == null || hashed == null) return false;
        
        // compare plain password to stored BCrypt hash
        return BCrypt.checkpw(plainPassword, hashed);
    }
}