package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.employdemy.library.lms.model.TransactionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PendingResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookISBN;
    private LocalDate dueDate;
    private String type;
}
