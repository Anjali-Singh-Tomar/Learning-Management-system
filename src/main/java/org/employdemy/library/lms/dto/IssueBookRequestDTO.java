package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class IssueBookRequestDTO {

    Long userId;
    Long bookId;
    LocalDate dueDate;
}
