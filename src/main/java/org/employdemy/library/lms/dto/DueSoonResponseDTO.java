package org.employdemy.library.lms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DueSoonResponseDTO {
    private Long borrowId;
    private String memberName;
    private String bookTitle;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private long daysRemaining;
}
