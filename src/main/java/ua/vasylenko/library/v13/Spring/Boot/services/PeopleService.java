package ua.vasylenko.library.v13.Spring.Boot.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.vasylenko.library.v13.Spring.Boot.models.Book;
import ua.vasylenko.library.v13.Spring.Boot.models.Person;
import ua.vasylenko.library.v13.Spring.Boot.repositories.PeopleRepository;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> getPeople() {
        return peopleRepository.findAll();
    }

    public Page<Person> getPeople(int pageNumber, int pageSize, String sort) {
        return peopleRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(sort)));
    }

    @Transactional
    public void create(Person person) {
        person.setRole("USER");
        person.setPassword(passwordEncoder.encode("111"));
        peopleRepository.save(person);
    }

    public Person getPerson(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    public Optional<Person> getPerson(String email) {
        return peopleRepository.findByEmail(email);
    }

    @Transactional
    public void update(int userId, Person person) {
        Person personFromDb = peopleRepository.findById(userId).orElse(null);
        person.setId(userId);
        person.setPassword(personFromDb.getPassword());
        person.setBooks(personFromDb.getBooks());
        if(person.getRole() == null)
            person.setRole(personFromDb.getRole());
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(int userId) {
        peopleRepository.deleteById(userId);
    }

    public List<Book> getBooksByPerson(int id) {
        Person person = getPerson(id);
        Hibernate.initialize(person.getBooks());
        person.getBooks().forEach(book -> {
            long diffInMillis = new Date().getTime() - book.getTakenAt().getTime();
            if (diffInMillis > 864000000)  // 10 days
                book.setExpired(true);
        });
        return person.getBooks();
    }

    @Transactional
    public void registration(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("USER");
        peopleRepository.save(person);
    }

    public List<Person> findByNameContainingIgnoreCase(String query) {
        return peopleRepository.findByNameContainingIgnoreCase(query);
    }
}
