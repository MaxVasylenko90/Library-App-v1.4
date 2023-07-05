package ua.vasylenko.library.v13.Spring.Boot.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.vasylenko.library.v13.Spring.Boot.DTO.PersonDTO;
import ua.vasylenko.library.v13.Spring.Boot.models.Person;
import ua.vasylenko.library.v13.Spring.Boot.security.PersonDetails;
import ua.vasylenko.library.v13.Spring.Boot.services.PeopleService;
import ua.vasylenko.library.v13.Spring.Boot.util.CreateByUser;
import ua.vasylenko.library.v13.Spring.Boot.util.PersonDTOValidator;

@Controller
public class MainController {
    private final PersonDTOValidator personDTOValidator;
    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public MainController(PersonDTOValidator personDTOValidator, PeopleService peopleService, ModelMapper modelMapper) {
        this.personDTOValidator = personDTOValidator;
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/")
    public String mainPage(RedirectAttributes redirectAttributes, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        if (personDetails.getPerson().getRole().equals("ADMIN")) {
            model.addAttribute("name", personDetails.getPerson().getName());
            return "mainPage";
        } else {
            redirectAttributes.addAttribute("id", personDetails.getPerson().getId());
            model.addAttribute("name", personDetails.getPerson().getName());
            return "redirect:/people/{id}";
        }
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") PersonDTO personDTO) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String userRegistration(@ModelAttribute("person") @Valid PersonDTO personDTO, BindingResult bindingResult) {
        personDTOValidator.validate(personDTO, bindingResult);
        if (bindingResult.hasErrors())
            return "/auth/registration";
        peopleService.registration(modelMapper.map(personDTO, Person.class));
        return "redirect:/auth/login";
    }
}
