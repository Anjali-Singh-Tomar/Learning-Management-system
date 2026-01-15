package org.employdemy.library.lms.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminReportsOverview {

    private Long inActiveUsers;
    private Long booksBorrowed;
    private Long newMembers;
    private Long lessBooks;
}
