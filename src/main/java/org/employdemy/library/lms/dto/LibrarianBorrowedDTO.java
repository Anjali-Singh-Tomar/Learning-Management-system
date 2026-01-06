package org.employdemy.library.lms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LibrarianBorrowedDTO {


    private Long id;
    private String userName;
    private String bookName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String status;
}
