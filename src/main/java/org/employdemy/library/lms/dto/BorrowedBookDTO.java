package org.employdemy.library.lms.dto;

import lombok.Data;
import org.employdemy.library.lms.model.TransactionStatus;

import java.time.LocalDate;

@Data
public class BorrowedBookDTO {

    private Long transactionId;
    private Long bookId;
    private String title;
    private String author;
    private LocalDate borrowedAt;
    private LocalDate returnedAt;
    private LocalDate dueDate;
    private TransactionStatus status;

}
