package org.employdemy.library.lms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionRequestDTO {

    @NotNull
    private Long userId;

    @NotNull
    private Long bookId;

    // getters & setters
}
