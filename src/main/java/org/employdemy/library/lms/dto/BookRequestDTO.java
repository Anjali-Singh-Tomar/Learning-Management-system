package org.employdemy.library.lms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.employdemy.library.lms.model.Genre;

@Data
public class BookRequestDTO {

    @NotBlank
    private String title;

    private String author;

    @NotBlank
    private String isbn;

    private Genre genre;

    private String publisher;

    private Integer publishedYear;

    private String imageName;
    private String imageType;
    private byte[] imageData;

    @Min(1)
    private Integer totalCopies;
}
