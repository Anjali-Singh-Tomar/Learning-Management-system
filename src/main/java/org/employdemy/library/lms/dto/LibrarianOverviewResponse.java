package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LibrarianOverviewResponse {

    private long todayIssued;
    private long todayReturned;
    private long dueToday;
    private long activeMembers;
}
