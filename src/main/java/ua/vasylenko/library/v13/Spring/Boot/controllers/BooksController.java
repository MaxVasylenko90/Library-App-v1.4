package ua.vasylenko.library.v13.Spring.Boot.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.vasylenko.library.v13.Spring.Boot.DTO.*;
import ua.vasylenko.library.v13.Spring.Boot.models.Book;
import ua.vasylenko.library.v13.Spring.Boot.models.Person;
import ua.vasylenko.library.v13.Spring.Boot.services.BooksService;
import ua.vasylenko.library.v13.Spring.Boot.services.PeopleService;
import ua.vasylenko.library.v13.Spring.Boot.util.BookDTOValidator;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final PeopleService peopleService;
    private final BooksService booksService;
    private final ModelMapper modelMapper;
    private final BookDTOValidator bookDTOValidator;

    @Autowired
    public BooksController(PeopleService peopleService, BooksService booksService, ModelMapper modelMapper, BookDTOValidator bookDTOValidator) {
        this.peopleService = peopleService;
        this.booksService = booksService;
        this.modelMapper = modelMapper;
        this.bookDTOValidator = bookDTOValidator;
    }

    @GetMapping
    public String bookList(Model model, @RequestParam(value = "page") Optional<Integer> pageNumber,
                           @RequestParam(value = "books_per_page", defaultValue = "5") Integer pageSize,
                           @RequestParam(value = "sort_by", defaultValue = "name") Optional<String> sort) {
            if (pageNumber.isEmpty())
                return "redirect:/books?page=0&books_per_page=5";
            Page page = booksService.findAll(pageNumber.get(), pageSize, sort.get());
            BooksResponse bookList = new BooksResponse(convertToListDTO(page.getContent()));
            model.addAttribute("bookList", bookList);
            model.addAttribute("currentPage", pageNumber.get() + 1);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("sortSuffix", sort.get());
        return "books/index";
    }

    @GetMapping("/search")
    public String search() {
        return "books/search";
    }

    @PostMapping("/search")
    public String searchResult(Model model, @RequestParam("searchQuery") String query) {
        model.addAttribute("bookList", new BooksResponse(convertToListDTO(booksService.findByNameContainingIgnoreCase(query))));
        return "books/search";
    }

    @GetMapping("/new")
    public String addNewBook(@ModelAttribute("book") BookDTO book) {
        return "books/newBook";
    }

    @PostMapping()
    public String newBook(@ModelAttribute("book") @Valid BookDTO book, BindingResult bindingResult) {
        bookDTOValidator.validate(book, bindingResult);
        if (bindingResult.hasErrors())
            return "books/newBook";
        booksService.addNewBook(modelMapper.map(book, Book.class));
        return "redirect:/books?page=0&books_per_page=5";
    }

    @GetMapping("/{id}")
    public String bookPage(@PathVariable("id") int id, @ModelAttribute("editedPerson") PersonDTOAllFields person, Model model) {
        Book tmpBook = booksService.getBook(id);
        BookDTOAllFields book = modelMapper.map(tmpBook, BookDTOAllFields.class);
        model.addAttribute("book", book);
        if (tmpBook.getPerson() != null)
            model.addAttribute("owner", modelMapper.map(book.getPerson(), PersonDTOAllFields.class));
        else model.addAttribute("people", new PeopleResponse(peopleService.getPeople()
                    .stream().map(this::convertToPersonDTO).collect(Collectors.toList())));
        return "books/bookPage";
    }

    @PatchMapping("/{id}/assign")
    public String bookAssign(@PathVariable("id") int bookId, @ModelAttribute("editedPerson")  PersonDTOAllFields person) {
        booksService.assignPerson(bookId, person.getId());
        return "redirect:/books/{id}";
    }

    @PatchMapping("/{id}/release")
    public String releaseBook(@PathVariable("id") int bookId) {
        booksService.releaseBook(bookId);
        return "redirect:/books/{id}";
    }

    @DeleteMapping("/{id}/delete")
    public String deleteBook(@PathVariable("id") int id) {
        booksService.deleteBook(id);
        return "redirect:/books?page=0&books_per_page=5";
    }

    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable("id") int bookId, Model model) {
        model.addAttribute("book", modelMapper.map(booksService.getBook(bookId), BookDTOAllFields.class));
        return "books/editBook";
    }

    @PatchMapping("/{id}")
    public String updateBook(@PathVariable("id") int bookId, @ModelAttribute("book") @Valid BookDTO book) {
        booksService.update(bookId, modelMapper.map(book, Book.class));
        return "redirect:/books/{id}";
    }

    private BookDTOAllFields convertToBookDTOAllFields(Book book) {
        return modelMapper.map(book, BookDTOAllFields.class);
    }
    private List<BookDTOAllFields> convertToListDTO(List<Book> books) {
        return books.stream().map(this :: convertToBookDTOAllFields).collect(Collectors.toList());
    }

    public PersonDTOAllFields convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTOAllFields.class);
    }

}
