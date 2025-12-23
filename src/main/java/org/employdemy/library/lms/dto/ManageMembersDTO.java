package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManageMembersDTO {

    private String name;
    private String email;
    private Long borrowedCount;
    private String status;
}
