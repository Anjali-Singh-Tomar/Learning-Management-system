package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOverviewResponse {
    private long totalBooks;
    private long borrowedBooks;
    private long overdueBooks;
    private long activeUsers;
}

