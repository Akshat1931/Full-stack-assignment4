package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data Access Layer (Repository) for Book Entity.
 * Spring Data JPA provides automated implementations of CRUD operations at runtime.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategoryIgnoreCase(String category);
}
