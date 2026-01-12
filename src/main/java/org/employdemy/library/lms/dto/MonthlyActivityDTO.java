package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyActivityDTO {

    private String label;//months
    private int borrowCount;
    private int returnCount;
}
