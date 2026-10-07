package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Layer (REST Controller) for Book management.
 * Delegates all business logic to the BookService.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // Dependency Injection of Service Layer into Controller
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Endpoint 1: Get all books
     * GET /api/books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Endpoint 2: Get book by ID
     * GET /api/books/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint 3: Filter books by category
     * GET /api/books/search?category=Technology
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> getBooksByCategory(@RequestParam String category) {
        return ResponseEntity.ok(bookService.getBooksByCategory(category));
    }

    /**
     * Endpoint 4: Add a new book
     * POST /api/books
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }
}
