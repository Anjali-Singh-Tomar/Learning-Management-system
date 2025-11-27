package org.employdemy.library.lms.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.employdemy.library.lms.model.Genre;

@Data
public class BookRequestDTO {

    @NotBlank(message = "Book title cannot be empty")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Author name cannot be empty")
    @Size(max = 100, message = "Author name cannot exceed 100 characters")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Pattern(
            regexp = "^(97(8|9))?\\d{9}(\\d|X)$",
            message = "Invalid ISBN format"
    )
    private String isbn;

    @NotNull(message = "Genre must be selected")
    private Genre genre;

    @Size(max = 100, message = "Publisher name cannot exceed 100 characters")
    private String publisher;

    @Min(value = 1500, message = "Published year looks invalid")
    @Max(value = 2100, message = "Published year cannot be in the far future")
    private Integer publishedYear;

    @NotNull(message = "Total copies must be provided")
    @Min(value = 1, message = "At least one copy is required")
    private Integer totalCopies;
}
