package org.example.model;

import java.util.UUID;

public class UserEntity {
    private final UUID uuid;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private Boolean isBlocked;

    public UserEntity() {
        this.uuid = UUID.randomUUID();
        this.role = UserRole.USER;
    }

    public UserEntity(UUID uuid) {
        this.uuid = uuid;
        this.role = UserRole.USER;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }
}