package org.employdemy.library.lms.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.employdemy.library.lms.model.Genre;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManageBooksDTO {

    private String title;

    private String author;

    private String isbn;

    private Genre genre;

    private String status;

    private Integer availableCopies;
}
