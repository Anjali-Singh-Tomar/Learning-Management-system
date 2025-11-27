package org.employdemy.library.lms.dto;

import lombok.Data;
import org.employdemy.library.lms.model.Role;

@Data
public class AuthResponseDTO {

    private String token;
    private Long userId;      // null for hard-coded admin
    private String name;
    private String email;
    private Role role;
}
