package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.employdemy.library.lms.model.Role;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManageMembersDTO {

    private Long id;
    private String memberName;
    private String memberId;
    private String memberEmail;
    private LocalDate joiningDate;
    private Long borrowedCount;
    private Long totalBorrowed;
    private Long overdue;
    private String status;
    private Role role;
}
