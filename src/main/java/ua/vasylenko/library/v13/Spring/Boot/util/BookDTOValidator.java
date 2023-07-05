package ua.vasylenko.library.v13.Spring.Boot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.vasylenko.library.v13.Spring.Boot.DTO.BookDTO;
import ua.vasylenko.library.v13.Spring.Boot.models.Book;
import ua.vasylenko.library.v13.Spring.Boot.services.BooksService;


@Component
public class BookDTOValidator implements Validator {
    private final BooksService booksService;

    @Autowired
    public BookDTOValidator(BooksService booksService) {
        this.booksService = booksService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return BookDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BookDTO book = (BookDTO) target;
        if (booksService.getBook(book.getName()).isPresent())
            errors.rejectValue("name", "", "This name is already taken");
    }
}
