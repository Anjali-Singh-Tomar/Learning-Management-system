package org.employdemy.library.lms.dto;

import lombok.Data;
import org.employdemy.library.lms.model.TransactionStatus;

import java.time.LocalDate;

@Data
public class TransactionResponseDTO {

    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
    private LocalDate returnedAt;
    private TransactionStatus status;

    // getters & setters
}
