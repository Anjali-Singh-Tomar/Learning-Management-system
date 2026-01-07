package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MemberDetailsDTO {

    private String memberName;
    private String memberId;
    private String memberEmail;
    private boolean active;
    private LocalDate joiningDate;
    private Long currentlyBorrowed;
    private Long totalBorrowed;
    private Long Overdue;
}
