package org.employdemy.library.lms.mapper;

import org.employdemy.library.lms.dto.NotificationResponseDTO;
import org.employdemy.library.lms.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toDto(Notification notification) {

        if (notification == null) {
            return null;
        }

        return new NotificationResponseDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isReadFlag(),
                notification.getCreatedAt()
        );
    }

    public List<NotificationResponseDTO> toDtoList(List<Notification> notifications) {
        return notifications.stream()
                .map(this::toDto)
                .toList();
    }
}
