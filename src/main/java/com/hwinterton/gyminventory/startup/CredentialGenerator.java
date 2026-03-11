/*
 * Purpose:
 * - generates temporary passwords for first run seeded accounts
 * 
 * Function:
 * - builds random passwords using letters, numbers, and symbols
 * - supports secure temporary credential generation
 * 
 * Dependencies:
 * - SecureRandom
 */

package com.hwinterton.gyminventory.startup;

import java.security.SecureRandom;

public final class CredentialGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int DEFAULT_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CredentialGenerator() {}

    // Method - generate random temporary password
    public static String generatePassword() {
        StringBuilder sb = new StringBuilder(DEFAULT_LENGTH);

        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }

        return sb.toString();
    }
}
