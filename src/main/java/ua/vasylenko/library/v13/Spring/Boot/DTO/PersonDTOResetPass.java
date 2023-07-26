package ua.vasylenko.library.v13.Spring.Boot.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PersonDTOResetPass {
    @NotNull(message = "Create a password!")
    private String password;
    @NotEmpty(message = "You should to specify your email!")
    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
