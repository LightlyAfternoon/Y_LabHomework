package org.example.controller.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.annotation.Default;
import org.example.model.UserRole;

@Getter
public class UserDTO {
    private int id;
    @Setter
    private String name;
    @Setter
    private String email;
    @Setter
    private String password;
    @Setter
    private UserRole role;
    @Setter
    private boolean isBlocked;

    private UserDTO(UserBuilder builder) {
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

        public UserBuilder(String email, String password, String name) {
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

        public UserDTO build() {
            return new UserDTO(this);
        }
    }

    public UserDTO() {
        this.role = UserRole.USER;
        this.isBlocked = false;
    }

    @Default
    public UserDTO(int id) {
        this.id = id;
        this.role = UserRole.USER;
        this.isBlocked = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserDTO)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        UserDTO user = (UserDTO) obj;

        return this.name.equals(user.name) &&
                this.email.equals(user.email) &&
                this.role == user.role &&
                this.isBlocked == user.isBlocked;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();

        result = 31 * result + email.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + (isBlocked ? 1 : 0);

        return result;
    }

    @Override
    public String toString() {
        return "id: " + this.getId() + " Имя: " + this.getName() + " Email: " + this.getEmail() + " Роль: " + this.getRole();
    }

    public static boolean isValid(UserDTO userDTO) {
        return (userDTO.getEmail() != null && !userDTO.getEmail().isBlank()) &&
                (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) &&
                (userDTO.getName() != null && !userDTO.getName().isBlank());
    }
}