package ua.vasylenko.library.v13.Spring.Boot.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.vasylenko.library.v13.Spring.Boot.DTO.PersonDTO;
import ua.vasylenko.library.v13.Spring.Boot.DTO.PersonDTOAllFields;
import ua.vasylenko.library.v13.Spring.Boot.models.Person;
import ua.vasylenko.library.v13.Spring.Boot.services.PeopleService;

import java.util.Optional;

@Component
public class PersonDTOAllFieldsValidator implements Validator {
    private final PeopleService peopleService;

    @Autowired
    public PersonDTOAllFieldsValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonDTOAllFields.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonDTOAllFields personDTO = (PersonDTOAllFields) target;
        Person personFromDb = peopleService.getPerson(personDTO.getEmail()).orElse(null);
        if (personFromDb != null)
            if (personFromDb.getId() != personDTO.getId())
                errors.rejectValue("email", "", "This email is already taken!");
    }
}
