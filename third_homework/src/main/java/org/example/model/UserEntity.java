package org.example.model;

public class UserEntity {
    private int id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private boolean isBlocked;

    private UserEntity(UserBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
        this.isBlocked = builder.isBlocked;
    }

    public static class UserBuilder {
        private int id;
        private String name;
        private String email;
        private String password;
        private UserRole role;
        private boolean isBlocked;

        public UserBuilder(String email, String password ,String name) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = UserRole.USER;
            this.isBlocked = false;
        }

        public UserBuilder id(int id) {
            this.id = id;

            return this;
        }

        public UserBuilder role(UserRole role) {
            this.role = role;

            return this;
        }

        public UserBuilder isBlocked(Boolean isBlocked) {
            this.isBlocked = isBlocked;

            return this;
        }

        public UserEntity build() {
            return new UserEntity(this);
        }
    }

    public UserEntity() {
        this.role = UserRole.USER;
        this.isBlocked = false;
    }

    public UserEntity(int id) {
        this.id = id;
        this.role = UserRole.USER;
        this.isBlocked = false;
    }

    public int getId() {
        return id;
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

    public boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
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

    @Override
    public String toString() {
        return "id: " + this.getId() + " Имя: " + this.getName() + " Email: " + this.getEmail() + " Роль: " + this.getRole();
    }
}