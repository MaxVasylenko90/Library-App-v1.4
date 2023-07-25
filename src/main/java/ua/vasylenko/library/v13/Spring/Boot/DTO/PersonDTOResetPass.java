package ua.vasylenko.library.v13.Spring.Boot.DTO;

import jakarta.validation.constraints.NotNull;

public class PersonDTOResetPass {
    @NotNull(message = "Create a password!")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
