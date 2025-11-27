package org.employdemy.library.lms.dto;

import lombok.Data;

@Data
public class DashboardResponseDTO {

    private long totalBooks;
    private long booksBorrowed;
    private long overdue;
    private long activeMembers;
}
