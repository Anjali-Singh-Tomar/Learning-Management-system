package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyBooksDTO {

    private Long transactionId;
    private Long bookId;
    private String title;
    private String author;

    private String borrowedAt;
    private String dueDate;

    private int daysLeft;
    private String dueStatus;

    private String imageBase64;



}
