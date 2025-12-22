package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class IssueBookResponseDTO {

    private Long transactionId;
    private String memberName;
    private String bookTitle;
    private LocalDate dueDate;
    private String message;
}
