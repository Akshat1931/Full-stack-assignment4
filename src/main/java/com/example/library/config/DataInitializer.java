package com.example.library.config;

import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Initializes in-memory seed data on Spring Boot startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public DataInitializer(BookRepository bookRepository,
                           MemberRepository memberRepository,
                           BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @Override
    public void run(String... args) {
        // Seed Books
        Book b1 = new Book("Clean Code", "Robert C. Martin", "978-0132350884", "Software", 3, 3);
        Book b2 = new Book("Spring in Action", "Craig Walls", "978-1617294945", "Software", 1, 1);
        Book b3 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", "Fiction", 2, 2);
        bookRepository.save(b1);
        bookRepository.save(b2);
        bookRepository.save(b3);

        // Seed Members
        Member m1 = new Member("Alice Johnson", "alice@example.com", "STANDARD", 2);
        Member m2 = new Member("Bob Smith", "bob@example.com", "PREMIUM", 5);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // Seed an overdue borrow record for Bob to demonstrate fine calculation
        // Book 3 was borrowed 20 days ago, due 6 days ago (overdue by 6 days)
        LocalDate borrowDate = LocalDate.now().minusDays(20);
        LocalDate dueDate = borrowDate.plusDays(14); // 6 days ago
        BorrowRecord overdueRecord = new BorrowRecord(b3.getId(), m2.getId(), borrowDate, dueDate);
        borrowRecordRepository.save(overdueRecord);
        m2.setBorrowedBooksCount(1);
        memberRepository.save(m2);
        b3.setAvailableCopies(b3.getAvailableCopies() - 1);
        bookRepository.save(b3);

        System.out.println(">>> [LibraryApp] Initial sample data successfully seeded into H2 Database.");
    }
}
