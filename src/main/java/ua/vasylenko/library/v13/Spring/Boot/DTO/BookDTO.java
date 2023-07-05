package ua.vasylenko.library.v13.Spring.Boot.DTO;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class BookDTO {
    @NotEmpty(message = "The name should be specified")
    private String name;
    @Size(min = 1, max = 50, message = "Author name should be greater than 1 character!")
    @NotEmpty(message = "The author should be specified")
    private String author;
    @Min(value = 1000, message = "Year must be greater than 1000")
    @Max(value = 2024, message = "Year must be less than 2024")
    private int year;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
