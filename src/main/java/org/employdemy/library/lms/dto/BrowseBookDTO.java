package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.model.Genre;
import org.employdemy.library.lms.model.TransactionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrowseBookDTO {

    private Long id;
    private String title;
    private String author;
    private Genre genre;
    private String imageName;
    private String imageType;
    private String imageBase64;
    private boolean active;
    private String requestStatus;//REQUESTED, BORROWED
}
