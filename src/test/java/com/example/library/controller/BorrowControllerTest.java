package com.example.library.controller;

import com.example.library.dto.BorrowRequest;
import com.example.library.dto.ReturnResponse;
import com.example.library.entity.BorrowRecord;
import com.example.library.service.BorrowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowController.class)
public class BorrowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowService borrowService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("REST Controller: POST /api/borrow creates borrow transaction")
    void testBorrowBookEndpoint() throws Exception {
        BorrowRecord record = new BorrowRecord(1L, 2L, LocalDate.now(), LocalDate.now().plusDays(14));
        record.setId(50L);

        when(borrowService.borrowBook(2L, 1L)).thenReturn(record);

        BorrowRequest request = new BorrowRequest(2L, 1L);

        mockMvc.perform(post("/api/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50L))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.bookId").value(1L))
                .andExpect(jsonPath("$.memberId").value(2L));
    }

    @Test
    @DisplayName("REST Controller: POST /api/borrow/return/{id} returns book and calculates fine")
    void testReturnBookEndpoint() throws Exception {
        ReturnResponse response = new ReturnResponse(50L, 1L, 2L, LocalDate.now(), 5, 10.0, "Book returned with overdue fine.");

        when(borrowService.returnBook(50L)).thenReturn(response);

        mockMvc.perform(post("/api/borrow/return/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordId").value(50L))
                .andExpect(jsonPath("$.daysOverdue").value(5))
                .andExpect(jsonPath("$.fineAmount").value(10.0));
    }

    @Test
    @DisplayName("REST Controller: GET /api/borrow returns list of transactions")
    void testGetAllBorrowRecordsEndpoint() throws Exception {
        BorrowRecord record = new BorrowRecord(1L, 2L, LocalDate.now(), LocalDate.now().plusDays(14));
        record.setId(50L);

        when(borrowService.getAllBorrowRecords()).thenReturn(List.of(record));

        mockMvc.perform(get("/api/borrow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(50L));
    }
}
