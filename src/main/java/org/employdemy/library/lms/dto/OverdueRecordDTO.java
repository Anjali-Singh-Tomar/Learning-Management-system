package org.employdemy.library.lms.dto;

import lombok.Data;

@Data
public class OverdueRecordDTO {

    private Long transactionId;
    private String bookTitle;
    private String isbn;
    private String borrowerName;
    private String borrowerEmail;

    private String dueDate;
    private long daysOverdue;
}

