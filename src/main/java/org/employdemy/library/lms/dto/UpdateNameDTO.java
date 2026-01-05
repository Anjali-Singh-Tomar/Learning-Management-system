package org.employdemy.library.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNameDTO {

    @NotBlank(message = "Name cannot be empty")
    private String name;
}
