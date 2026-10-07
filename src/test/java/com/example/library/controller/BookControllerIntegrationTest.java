package com.example.library.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Integration Test: GET /api/books returns seeded list of books")
    void testGetAllBooksEndpoint() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].title", notNullValue()));
    }

    @Test
    @DisplayName("Integration Test: POST /api/books creates a new book and returns 201 Created")
    void testCreateBookEndpoint() throws Exception {
        String newBookJson = """
                {
                    "title": "Design Patterns",
                    "author": "Gang of Four",
                    "isbn": "978-0201633610",
                    "category": "Architecture",
                    "totalCopies": 5,
                    "availableCopies": 5
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Design Patterns")))
                .andExpect(jsonPath("$.category", is("Architecture")));
    }
}
