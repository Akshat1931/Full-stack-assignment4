package com.example.library.service;

import com.example.library.dto.ReturnResponse;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BorrowServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @InjectMocks
    private BorrowService borrowService;

    private Book testBook;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testBook = new Book("Effective Java", "Joshua Bloch", "978-0134685991", "Software", 3, 2);
        testBook.setId(1L);

        testMember = new Member("John Doe", "john@example.com", "STANDARD", 2);
        testMember.setId(10L);
    }

    @Test
    @DisplayName("Business Rule 1: Successful book borrowing when copies available and member within limit")
    void testBorrowBook_Success() {
        when(memberRepository.findById(10L)).thenReturn(Optional.of(testMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowRecord record = borrowService.borrowBook(10L, 1L);

        assertNotNull(record);
        assertEquals(1L, record.getBookId());
        assertEquals(10L, record.getMemberId());
        assertEquals("ACTIVE", record.getStatus());
        assertEquals(1, testBook.getAvailableCopies()); // 2 decremented to 1
        assertEquals(1, testMember.getBorrowedBooksCount()); // 0 incremented to 1

        verify(bookRepository, times(1)).save(testBook);
        verify(memberRepository, times(1)).save(testMember);
        verify(borrowRecordRepository, times(1)).save(any(BorrowRecord.class));
    }

    @Test
    @DisplayName("Business Rule 1: Borrowing rejected when member reaches max quota")
    void testBorrowBook_ExceedLimit_ThrowsException() {
        testMember.setBorrowedBooksCount(2); // Limit is 2

        when(memberRepository.findById(10L)).thenReturn(Optional.of(testMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            borrowService.borrowBook(10L, 1L);
        });

        assertTrue(exception.getMessage().contains("Borrow limit exceeded"));
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    @DisplayName("Business Rule 1: Borrowing rejected when book is out of stock")
    void testBorrowBook_OutOfStock_ThrowsException() {
        testBook.setAvailableCopies(0); // No available copies

        when(memberRepository.findById(10L)).thenReturn(Optional.of(testMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            borrowService.borrowBook(10L, 1L);
        });

        assertTrue(exception.getMessage().contains("currently out of stock"));
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    @DisplayName("Business Rule 2: Overdue fine calculated correctly upon return")
    void testReturnBook_OverdueFineCalculation() {
        LocalDate borrowDate = LocalDate.now().minusDays(20);
        LocalDate dueDate = borrowDate.plusDays(14); // Due 6 days ago
        LocalDate returnDate = LocalDate.now();      // 6 days overdue

        BorrowRecord record = new BorrowRecord(1L, 10L, borrowDate, dueDate);
        record.setId(100L);

        testMember.setBorrowedBooksCount(1);

        when(borrowRecordRepository.findById(100L)).thenReturn(Optional.of(record));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(10L)).thenReturn(Optional.of(testMember));

        ReturnResponse response = borrowService.returnBook(100L, returnDate);

        assertNotNull(response);
        assertEquals(6, response.getDaysOverdue());
        assertEquals(12.0, response.getFineAmount()); // 6 days * $2.00 = $12.00
        assertEquals("RETURNED", record.getStatus());
        assertEquals(3, testBook.getAvailableCopies()); // Restored
        assertEquals(0, testMember.getBorrowedBooksCount()); // Decremented
    }
}
