package org.employdemy.library.lms.dto;

import lombok.*;
import org.employdemy.library.lms.model.TransactionStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TransactionResponseDTO {


    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
    private LocalDate returnedAt;
    private TransactionStatus status;

    // getters & setters
}
