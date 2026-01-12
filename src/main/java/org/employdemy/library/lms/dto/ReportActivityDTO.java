package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportActivityDTO {

    private String label;//months
    private int count;
}
