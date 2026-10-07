package com.example.library.service;

import com.example.library.dto.ReturnResponse;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Business Layer (Service) implementing core business rules for library transactions.
 */
@Service
public class BorrowService {

    public static final double DAILY_FINE_RATE = 2.0; // $2.00 per day overdue
    public static final int STANDARD_BORROW_DAYS = 14;

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public BorrowService(BookRepository bookRepository,
                         MemberRepository memberRepository,
                         BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    /**
     * BUSINESS RULE 1:
     * Check member borrow eligibility and book availability before issuing a book.
     * - Member must exist.
     * - Member cannot exceed their maxBorrowLimit.
     * - Book must have available copies > 0.
     * - Updates book inventory and member count atomically.
     */
    @Transactional
    public BorrowRecord borrowBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

        // Rule 1a: Check member borrow quota
        if (member.getBorrowedBooksCount() >= member.getMaxBorrowLimit()) {
            throw new IllegalStateException("Borrow limit exceeded. Member has already borrowed "
                    + member.getBorrowedBooksCount() + " of maximum allowed " + member.getMaxBorrowLimit() + " books.");
        }

        // Rule 1b: Check available copies
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Book '" + book.getTitle() + "' is currently out of stock.");
        }

        // Update domain state
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        member.setBorrowedBooksCount(member.getBorrowedBooksCount() + 1);
        memberRepository.save(member);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(STANDARD_BORROW_DAYS);

        BorrowRecord record = new BorrowRecord(bookId, memberId, borrowDate, dueDate);
        return borrowRecordRepository.save(record);
    }

    /**
     * BUSINESS RULE 2:
     * Book return processing and overdue fine calculation.
     * - Record must exist and must be in "ACTIVE" state.
     * - If returned past due date, fine is calculated: overdue_days * DAILY_FINE_RATE.
     * - Restores book inventory and decrements member's borrowed count.
     */
    @Transactional
    public ReturnResponse returnBook(Long recordId, LocalDate returnDate) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow record not found with ID: " + recordId));

        if (!"ACTIVE".equalsIgnoreCase(record.getStatus())) {
            throw new IllegalStateException("Borrow record is already settled with status: " + record.getStatus());
        }

        Book book = bookRepository.findById(record.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + record.getBookId()));

        Member member = memberRepository.findById(record.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + record.getMemberId()));

        // Calculate fine if overdue
        long daysOverdue = 0;
        double fine = 0.0;
        if (returnDate.isAfter(record.getDueDate())) {
            daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
            fine = daysOverdue * DAILY_FINE_RATE;
        }

        // Update record
        record.setReturnDate(returnDate);
        record.setFineAmount(fine);
        record.setStatus("RETURNED");
        borrowRecordRepository.save(record);

        // Update book & member state
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        member.setBorrowedBooksCount(Math.max(0, member.getBorrowedBooksCount() - 1));
        memberRepository.save(member);

        String message = (daysOverdue > 0)
                ? "Book returned with overdue fine of $" + String.format("%.2f", fine) + " (" + daysOverdue + " days overdue)."
                : "Book returned successfully on time. No fines applied.";

        return new ReturnResponse(record.getId(), book.getId(), member.getId(), returnDate, daysOverdue, fine, message);
    }

    public ReturnResponse returnBook(Long recordId) {
        return returnBook(recordId, LocalDate.now());
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    public List<BorrowRecord> getBorrowRecordsByMember(Long memberId) {
        return borrowRecordRepository.findByMemberId(memberId);
    }
}
