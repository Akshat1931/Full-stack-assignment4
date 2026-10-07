package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business Layer (Service) managing Book operations.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;

    // Constructor-based Dependency Injection (Spring Boot Best Practice)
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category);
    }

    public Book saveBook(Book book) {
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot exceed total copies.");
        }
        return bookRepository.save(book);
    }
}
