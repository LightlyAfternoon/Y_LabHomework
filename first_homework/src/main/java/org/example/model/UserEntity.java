package org.example.model;

import java.util.UUID;

public class UserEntity {
    private final UUID uuid;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private boolean isBlocked;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserEntity)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        UserEntity user = (UserEntity) obj;

        return this.name.equals(user.name) &&
                this.email.equals(user.email) &&
                this.password.equals(user.password) &&
                this.role == user.role &&
                this.isBlocked == user.isBlocked;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();

        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + (isBlocked ? 1 : 0);

        return result;
    }

    public UserEntity getCopy() {
        UserEntity userEntityCopy = new UserEntity(this.uuid);

        userEntityCopy.name = this.name;
        userEntityCopy.email = this.email;
        userEntityCopy.password = this.password;
        userEntityCopy.role = this.role;
        userEntityCopy.isBlocked = this.isBlocked;

        return userEntityCopy;
    }

    @Override
    public String toString() {
        return "uuid: " + this.getUuid() + " Имя: " + this.getName() + " Email: " + this.getEmail() + " Роль: " + this.getRole();
    }
}