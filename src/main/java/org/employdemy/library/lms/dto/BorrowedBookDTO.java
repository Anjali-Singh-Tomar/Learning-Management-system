package org.employdemy.library.lms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowedBookDTO {

    private Long transactionId;
    private Long bookId;
    private String title;
    private String author;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
}
