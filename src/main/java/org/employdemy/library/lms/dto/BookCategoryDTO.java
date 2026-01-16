package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.employdemy.library.lms.model.Genre;

@Data
@AllArgsConstructor
public class BookCategoryDTO {

    private Genre genre;
    private Long count;
}
