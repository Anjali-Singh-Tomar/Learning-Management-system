package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDashboardResponseDTO {

    private long currentlyBorrowed;
    private long dueSoon;
    private long booksRead;
}
