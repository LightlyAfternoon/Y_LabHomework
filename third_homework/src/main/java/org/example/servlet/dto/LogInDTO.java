package org.example.servlet.dto;

public class LogInDTO {
    private String email;
    private String password;

    private LogInDTO(LogInBuilder builder) {
        this.email = builder.email;
        this.password = builder.password;
    }

    public static class LogInBuilder {
        private String email;
        private String password;

        public LogInBuilder(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public LogInDTO build() {
            return new LogInDTO(this);
        }
    }

    public LogInDTO() {}

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LogInDTO)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        LogInDTO user = (LogInDTO) obj;

        return this.email.equals(user.email) &&
                this.password.equals(user.password);
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();

        result = 31 * result + password.hashCode();

        return result;
    }
}