package ua.vasylenko.library.v13.Spring.Boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.vasylenko.library.v13.Spring.Boot.models.Book;


import java.util.List;
import java.util.Optional;
@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
        Optional<Book> findByName(String name);
        List<Book> findByNameContainingIgnoreCase(String name);
}
