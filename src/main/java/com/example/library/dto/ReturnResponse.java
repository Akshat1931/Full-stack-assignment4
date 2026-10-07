package com.example.library.dto;

import java.time.LocalDate;

public class ReturnResponse {
    private Long recordId;
    private Long bookId;
    private Long memberId;
    private LocalDate returnDate;
    private long daysOverdue;
    private double fineAmount;
    private String statusMessage;

    public ReturnResponse() {
    }

    public ReturnResponse(Long recordId, Long bookId, Long memberId, LocalDate returnDate,
                          long daysOverdue, double fineAmount, String statusMessage) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.returnDate = returnDate;
        this.daysOverdue = daysOverdue;
        this.fineAmount = fineAmount;
        this.statusMessage = statusMessage;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public long getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(long daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
