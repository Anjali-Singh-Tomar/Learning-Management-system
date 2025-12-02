package org.employdemy.library.lms.dto;

import lombok.Data;

@Data
public class RecommendedBookDTO {
    private Long id;
    private String title;
    private String author;
    private String genre;
    private Integer publishedYear;

    private String imageName;
    private String imageType;
    private String imageBase64;
}
