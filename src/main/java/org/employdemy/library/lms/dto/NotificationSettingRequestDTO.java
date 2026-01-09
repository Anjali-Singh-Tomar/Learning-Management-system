package org.employdemy.library.lms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationSettingRequestDTO {

    @NotNull
    private Long userId;

    private Boolean dueDateReminder;

    private Boolean newBooksReminder;

    private Boolean receiveEmail;
}
