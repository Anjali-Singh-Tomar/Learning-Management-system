package org.employdemy.library.lms.dto;


import java.time.LocalDate;

public record TodayActivityDTO(
        Long transactionId,
        String bookTitle,
        String memberName,
        LocalDate borrowedAt,
        String Type
){}
