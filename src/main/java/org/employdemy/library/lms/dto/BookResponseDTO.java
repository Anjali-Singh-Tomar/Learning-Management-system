package org.employdemy.library.lms.dto;

import lombok.Data;
import org.employdemy.library.lms.model.Genre;

@Data
public class BookResponseDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Genre genre;
    private String imageName;
    private String imageType;
    private String imageBase64;
    private String publisher;
    private Integer publishedYear;
    private Integer totalCopies;
    private Integer availableCopies;
    private boolean active;
}
