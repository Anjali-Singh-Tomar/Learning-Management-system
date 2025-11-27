package org.employdemy.library.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {

    // Can be email OR empId
    @NotBlank
    private String identifier;

    @NotBlank
    private String password;
}
