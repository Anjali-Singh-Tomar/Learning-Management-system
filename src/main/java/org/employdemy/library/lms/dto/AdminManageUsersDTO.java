package org.employdemy.library.lms.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.employdemy.library.lms.model.Role;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminManageUsersDTO {


    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDate joinDate;

    private Role role;

    private String status;
}
