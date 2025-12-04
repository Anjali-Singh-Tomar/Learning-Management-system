package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.employdemy.library.lms.model.Genre;

@Data
@AllArgsConstructor
public class RecommendedBookDTO {
    private Long id;
    private String title;
    private String author;
    private Genre genre;
    private Integer publishedYear;
    private String imageBase64;
    private String imageName;
    private String imageType;
}

