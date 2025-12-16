package org.employdemy.library.lms.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

public record PendingReturnDTO(
        Long transactionId,
        String memberName,
        String bookTitle,
        LocalDate dueDate,
        String dueLabel   // Today | Tomorrow | In 2 days
) {}
