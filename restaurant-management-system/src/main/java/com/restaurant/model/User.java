package com.restaurant.model;

import com.restaurant.enums.UserRole;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Abstract base class representing a System User.
 * Demonstrates OOP Encapsulation and Abstraction.
 */
public abstract class User {
    protected int userId;
    protected String username;
    protected String passwordHash;
    protected String salt;
    protected String fullName;
    protected String email;
    protected String phone;
    protected UserRole role;
    protected boolean active;
    protected LocalDateTime createdAt;

    public User() {
        this.active = true;
    }

    public User(int userId, String username, String fullName, String email, String phone, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.active = true;
    }

    /**
     * Abstract method demonstrating polymorphism across user roles.
     * @return list of permitted action names for this role.
     */
    public abstract List<String> getPermissions();

    public boolean hasPermission(String permission) {
        if (permission == null) return false;
        List<String> perms = getPermissions();
        return perms.contains(permission) || perms.contains("ALL");
    }

    // Encapsulation: Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
