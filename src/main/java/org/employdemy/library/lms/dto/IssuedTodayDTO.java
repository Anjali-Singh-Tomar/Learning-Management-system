package org.employdemy.library.lms.dto;


import java.time.LocalDate;

public record IssuedTodayDTO(
        Long transactionId,
        String bookTitle,
        String memberName,
        LocalDate borrowedAt
){}
