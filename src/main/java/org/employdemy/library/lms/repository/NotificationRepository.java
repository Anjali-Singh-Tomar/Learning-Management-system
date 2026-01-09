package org.employdemy.library.lms.repository;

import org.employdemy.library.lms.model.Notification;
import org.employdemy.library.lms.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    // Count unread notifications for a user
    Long countByUserIdAndReadFlagFalse(Long userId);

    // Get only unread notifications
    List<Notification> findByUserIdAndReadFlagFalseOrderByCreatedAtDesc(Long userId);

    // Get all request notifications
    List<Notification> findByUserIdAndType(Long userId, NotificationType type);

    // Delete all notifications of a user
    void deleteByUserId(Long userId);

    // Get all notifications for a user (newest first)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
