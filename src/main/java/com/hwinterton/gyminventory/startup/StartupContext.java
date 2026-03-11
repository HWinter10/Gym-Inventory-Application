/*
 * Purpose:
 * - temporarily stores first run credentials for display in setup screen
 * 
 * Function:
 * - holds generated owner and backup owner passwords in memory
 * - indicates whether current startup is first run
 * - clears values after first run screen is acknowledged
 * 
 * Dependencies:
 * - none
 */

package com.hwinterton.gyminventory.startup;

public final class StartupContext {

    private static boolean firstRun; // indicates whether app is currently on first run
    private static String ownerPassword; // generated password for owner account
    private static String backupOwnerPassword; // generated password for backup owner account

    private StartupContext() {}

    // Method - store generated first run passwords
    public static void setFirstRunCredentials(String ownerPw, String backupPw) {
        firstRun = true;
        ownerPassword = ownerPw;
        backupOwnerPassword = backupPw;
    }

    // Method - return whether app is currently in first run state
    public static boolean isFirstRun() {
        return firstRun;
    }

    // Method - return generated owner password
    public static String getOwnerPassword() {
        return ownerPassword;
    }

    // Method - return generated backup owner password
    public static String getBackupOwnerPassword() {
        return backupOwnerPassword;
    }

    // Method - clear first run values after setup screen is acknowledged
    public static void clear() {
        firstRun = false;
        ownerPassword = null;
        backupOwnerPassword = null;
    }
}