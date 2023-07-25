package ua.vasylenko.library.v13.Spring.Boot.controllers;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.vasylenko.library.v13.Spring.Boot.DTO.PersonDTO;
import ua.vasylenko.library.v13.Spring.Boot.DTO.PersonDTOResetPass;
import ua.vasylenko.library.v13.Spring.Boot.models.Person;
import ua.vasylenko.library.v13.Spring.Boot.security.PersonDetails;
import ua.vasylenko.library.v13.Spring.Boot.services.EmailService;
import ua.vasylenko.library.v13.Spring.Boot.services.PeopleService;
import ua.vasylenko.library.v13.Spring.Boot.util.PersonDTOValidator;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

@Controller
public class MainController {
    private final PersonDTOValidator personDTOValidator;
    private final PeopleService peopleService;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    @Autowired
    public MainController(PersonDTOValidator personDTOValidator, PeopleService peopleService,
                          ModelMapper modelMapper, EmailService emailService) {
        this.personDTOValidator = personDTOValidator;
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    @GetMapping("/auth/login")
    public String loginPage(@RequestParam(value = "message", required = false) String message, Model model) {
        if (message != null)
            model.addAttribute("message", message);
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

    @GetMapping("/recovery")
    public String recoverMyPassword(@RequestParam(value = "error", required = false) Boolean error, Model model) {
        if (error != null && error)
            model.addAttribute("message", "Incorrect email!");
        return "auth/recovery";
    }

    @PostMapping("/recovery")
    public String recoveryProcess(@RequestParam("email") String email,
                                  RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {
        Person personFromDb = peopleService.getPerson(email).orElse(null);
        if (personFromDb == null) {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/recovery";
        }
        this.emailService.sendSimpleMail(personFromDb.getName(), email, Locale.ENGLISH);
        return "auth/successSendEmail";
    }

    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email, Model model,
                                @ModelAttribute("user") PersonDTOResetPass user) {
        model.addAttribute("email", email);
        return "auth/newPasswordPage";
    }

    @PatchMapping("/resetPassword/{id}")
    public String resetPass(@PathVariable("id") String email, @ModelAttribute("user") PersonDTOResetPass user) {
        peopleService.updateUserPassword(email, user.getPassword());
        return "redirect:/auth/login";
    }


}
