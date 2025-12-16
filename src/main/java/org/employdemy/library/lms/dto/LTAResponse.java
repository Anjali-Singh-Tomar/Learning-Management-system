package org.employdemy.library.lms.dto;

import java.util.List;

public record LTAResponse (
        List<IssuedTodayDTO> issuedToday,
        List<ReturnedTodayDTO> returnedToday
){}
