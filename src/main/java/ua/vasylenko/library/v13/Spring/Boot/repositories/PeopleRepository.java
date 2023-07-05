package ua.vasylenko.library.v13.Spring.Boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.vasylenko.library.v13.Spring.Boot.models.Person;


import java.util.Optional;
@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);

    Optional<Person> findByName(String username);
}
