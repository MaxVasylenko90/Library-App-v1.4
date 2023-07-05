package ua.vasylenko.library.v13.Spring.Boot.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.NumberFormat;
import ua.vasylenko.library.v13.Spring.Boot.util.CreateByUser;


public class PersonDTO {
    @NotEmpty(message = "The name can't be empty!")
    private String name;

    @Min(value = 1900, message = "Year must be greater than 1900")
    @Max(value = 2024, message = "Year must be less than 2024")
    private int year;

    @NotEmpty(message = "You should to specify your email!")
    private String email;

    @NotNull(message = "Create a password!")
    private String password;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
}
