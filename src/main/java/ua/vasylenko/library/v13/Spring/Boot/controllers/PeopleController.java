package ua.vasylenko.library.v13.Spring.Boot.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.vasylenko.library.v13.Spring.Boot.DTO.*;
import ua.vasylenko.library.v13.Spring.Boot.models.Book;
import ua.vasylenko.library.v13.Spring.Boot.models.Person;
import ua.vasylenko.library.v13.Spring.Boot.security.PersonDetails;
import ua.vasylenko.library.v13.Spring.Boot.services.PeopleService;
import ua.vasylenko.library.v13.Spring.Boot.util.PersonDTOAllFieldsValidator;
import ua.vasylenko.library.v13.Spring.Boot.util.PersonDTOValidator;
import ua.vasylenko.library.v13.Spring.Boot.util.CreateByAdmin;

import java.util.Arrays;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final PersonDTOValidator personDTOValidator;
    private final PersonDTOAllFieldsValidator personDTOAllFieldsValidator;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonDTOValidator personDTOValidator, PersonDTOAllFieldsValidator personDTOAllFieldsValidator, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.peopleService = peopleService;
        this.personDTOValidator = personDTOValidator;
        this.personDTOAllFieldsValidator = personDTOAllFieldsValidator;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("peopleList", new PeopleResponse(peopleService.getPeople().stream()
                .map(this :: convertToPersonDTOWithAllFields).collect(Collectors.toList())));
        return "people/index";
    }


    @GetMapping("/new")
    public String newUser(@ModelAttribute("person") PersonDTO person) {
        return "people/newUser";
    }

    @PostMapping()
    public String createNewUser(@ModelAttribute("person") @Validated(CreateByAdmin.class) PersonDTO personDTO, BindingResult bindingResult) {
        personDTOValidator.validate(personDTO, bindingResult);
        if (bindingResult.hasErrors())
            return "people/newUser";
        peopleService.create(convertToPerson(personDTO));
        return "redirect:/people";
    }



    @GetMapping("/{id}")
    public String userProfile(@PathVariable("id") int id, Model model) {
        PersonDetails personDetails = getPersonDetails();
        if (personDetails.getPerson().getRole().equals("ADMIN") || personDetails.getPerson().getId() == id) {
            model.addAttribute("person", convertToPersonDTOWithAllFields(peopleService.getPerson(id)));
            model.addAttribute("books", new BooksResponse(peopleService.getBooksByPerson(id).stream()
                    .map(this ::convertToBookDTOWithAllFields).collect(Collectors.toList())));
            if (personDetails.getPerson().getRole().equals("ADMIN"))
                model.addAttribute("admin", convertToPersonDTOWithAllFields(personDetails.getPerson()));
            return "people/userPage";
        }
        return "people/accessDenied";
    }


    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable("id") int userId, Model model) {
        PersonDetails personDetails = getPersonDetails();
        if (personDetails.getPerson().getRole().equals("ADMIN") || personDetails.getPerson().getId() == userId) {
            if (personDetails.getPerson().getRole().equals("ADMIN"))
                model.addAttribute("admin", true);
            PersonDTOAllFields pers = convertToPersonDTOAllFields(peopleService.getPerson(userId));
            model.addAttribute("user", pers);
            model.addAttribute("roles", Arrays.asList("ADMIN", "USER"));
            return "people/editUser";
        }
        return "people/accessDenied";
    }



    @PatchMapping("/{id}")
    public String updateUser(@ModelAttribute("user") @Validated(CreateByAdmin.class) PersonDTOAllFields personDTO,
                             @PathVariable("id") int userId, BindingResult bindingResult) {
        personDTOAllFieldsValidator.validate(personDTO, bindingResult);
        if(bindingResult.hasErrors())
            return "people/editUser";
        peopleService.update(userId, convertToPerson(personDTO));
        return "redirect:/people/{id}";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") int userId) {
        peopleService.delete(userId);
        return "redirect:/people";
    }

    private PersonDTOAllFields convertToPersonDTOWithAllFields(Person person) {
        return modelMapper.map(person, PersonDTOAllFields.class);
    }

    private Person convertToPerson(PersonDTOAllFields personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private BookDTOAllFields convertToBookDTOWithAllFields(Book book) {
        return modelMapper.map(book, BookDTOAllFields.class);
    }
    private PersonDTOAllFields convertToPersonDTOAllFields(Person person) {
        return modelMapper.map(person, PersonDTOAllFields.class);
    }
    private static PersonDetails getPersonDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails;
    }
}