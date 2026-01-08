package org.employdemy.library.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.employdemy.library.lms.model.NotificationType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private String title;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private NotificationType type;
}

