package com.hwinterton.gyminventory.domain;

public class User {
    private final long id;
    private final String username;
    private final Role role;
    private final boolean active;
    private final boolean mustChangePassword;

    public User(long id, String username, Role role, boolean active, boolean mustChangePassword) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
        this.mustChangePassword = mustChangePassword;
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public boolean mustChangePassword() { return mustChangePassword; }
}