package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.NotificationResponseDTO;
import org.employdemy.library.lms.model.Notification;
import org.employdemy.library.lms.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ------------------------------------------------------------
    // GET ALL NOTIFICATIONS FOR A USER
    // ------------------------------------------------------------
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getNotificationsForUser(userId)
        );
    }

    // ------------------------------------------------------------
    // MARK ONE NOTIFICATION AS READ
    // ------------------------------------------------------------
    @PostMapping("/read/{id}")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }

    // ------------------------------------------------------------
    // MARK ALL NOTIFICATIONS OF A USER AS READ
    // ------------------------------------------------------------
    @PostMapping("/read-all/{userId}")
    public ResponseEntity<String> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    // ------------------------------------------------------------
    // DELETE ONE NOTIFICATION
    // ------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted");
    }

    // ------------------------------------------------------------
    // DELETE ALL NOTIFICATIONS OF A USER
    // ------------------------------------------------------------
    @DeleteMapping("/all/{userId}")
    public ResponseEntity<String> deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok("All notifications deleted for user");
    }
}

