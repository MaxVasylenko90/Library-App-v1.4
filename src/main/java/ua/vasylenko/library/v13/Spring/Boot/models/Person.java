package ua.vasylenko.library.v13.Spring.Boot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.NumberFormat;

import java.util.List;

@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
//    @Pattern(regexp = "[A-Z]\\w+ [A-Z]\\w+", message = "Ex. \"Maksym Vasylenko\"")
    @NotEmpty(message = "The name can't be empty!")
    private String name;
    @Column(name = "year")
    @Min(value = 1900, message = "Year must be greater than 1900")
    @Max(value = 2024, message = "Year must be less than 2024")
    private int year;
    @Column(name = "email")
    @NotEmpty(message = "You should to specify your email!")
    private String email;
    @Column(name = "password")
    @NotNull(message = "Create a good password to protect your account")
    private String password;
    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    private List<Book> books;
    @Column(name = "role")
    private String role;

    public Person() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
