package com.example.library.controller;

import com.example.library.dto.BorrowRequest;
import com.example.library.dto.ReturnResponse;
import com.example.library.entity.BorrowRecord;
import com.example.library.service.BorrowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Layer (REST Controller) for Borrow/Return transactions.
 * Orchestrates incoming HTTP requests and delegates processing to BorrowService.
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * Endpoint 1: Borrow a book
     * POST /api/borrow
     */
    @PostMapping
    public ResponseEntity<BorrowRecord> borrowBook(@RequestBody BorrowRequest request) {
        BorrowRecord record = borrowService.borrowBook(request.getMemberId(), request.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    /**
     * Endpoint 2: Return a borrowed book
     * POST /api/borrow/return/{recordId}
     */
    @PostMapping("/return/{recordId}")
    public ResponseEntity<ReturnResponse> returnBook(@PathVariable Long recordId) {
        ReturnResponse response = borrowService.returnBook(recordId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 3: View all borrow transactions
     * GET /api/borrow
     */
    @GetMapping
    public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
        return ResponseEntity.ok(borrowService.getAllBorrowRecords());
    }

    /**
     * Endpoint 4: View borrow records for a specific member
     * GET /api/borrow/member/{memberId}
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BorrowRecord>> getBorrowRecordsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowService.getBorrowRecordsByMember(memberId));
    }
}
