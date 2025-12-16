package org.employdemy.library.lms.dto;

import java.time.LocalDate;

public record ReturnedTodayDTO(
        Long transactionId,
        String bookTitle,
        String memberName,
        LocalDate returnedAt
) {}
