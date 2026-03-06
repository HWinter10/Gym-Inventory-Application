/*
 * Purpose:
 * - represents user summary view for administration screens
 * 
 * Function:
 * - stores basic user account information
 * - supports displaying user list in admin table
 * - excludes password hash and sensitive authentication data
 * 
 * Dependencies:
 * - Role enum
 */

package com.hwinterton.gyminventory.domain;

public class UserSummary {
    private final long id; // unique user id from database
    private final String username; // login name
    private final Role role; // system access role
    private final boolean active; // indicates account enabled status
    private final boolean mustChangePassword; // requires password change at next login

    // Method - construct user summary object
    public UserSummary(long id, String username, Role role, boolean active, boolean mustChangePassword) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
        this.mustChangePassword = mustChangePassword;
    }

    // Method - return user id
    public long getId() { return id; }
    
    // Method - return username
    public String getUsername() { return username; }
    
    // Method - return user role
    public Role getRole() { return role; }
    
    // Method - return active account status
    public boolean isActive() { return active; }
    
    // Method - return password change requirement
    public boolean mustChangePassword() { return mustChangePassword; }
}