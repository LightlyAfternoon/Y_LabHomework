package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.annotation.Default;
import org.hibernate.type.NumericBooleanConverter;

@Getter
@Entity
@Table(name = "user", schema = "service")
public class UserEntity extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_id")
    @SequenceGenerator(name = "seq_user_id", allocationSize = 1)
    private int id;
    @Setter
    private String name;
    @Setter
    private String email;
    @Setter
    private String password;
    @Setter
    @Column(name = "role_id")
    private UserRole role;
    @Setter
    @Column(name = "is_blocked")
    @Convert(converter = NumericBooleanConverter.class)
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

        public UserEntity build() {
            return new UserEntity(this);
        }
    }

    public UserEntity() {
        this.role = UserRole.USER;
        this.isBlocked = false;
    }

    @Default
    public UserEntity(int id) {
        this.id = id;
        this.role = UserRole.USER;
        this.isBlocked = false;
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