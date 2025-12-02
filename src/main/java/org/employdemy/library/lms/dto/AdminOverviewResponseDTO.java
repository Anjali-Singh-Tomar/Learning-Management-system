package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AdminOverviewResponseDTO {

    private long totalBooks;
    private long booksBorrowed;
    private long overdue;
    private long activeMembers;
}
