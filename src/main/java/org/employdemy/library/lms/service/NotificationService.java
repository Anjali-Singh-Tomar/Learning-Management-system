package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.model.Notification;
import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.NotificationRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void sendNotification(Long userId, String title, String message) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);

        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setReadFlag(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setReadFlag(true));
        notificationRepository.saveAll(list);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void deleteAllNotifications(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notificationRepository.deleteAll(list);
    }

    public void sendDueSoonReminder(Transaction tx) {
        String message = "Your borrowed book '" + tx.getBook().getTitle()
                + "' is due on " + tx.getDueDate();

           // TODO: send email or push
//        System.out.println("Sending reminder to user " + tx.getUser().getEmail() + ": " + message);

        sendNotification(tx.getUser().getId(), "Due Soon Books", message);
    }

    public void sendOverdueReminder(Transaction tx) {
        String message = "Your borrowed book '"
                + tx.getBook().getTitle()
                + "' was due on " + tx.getDueDate()
                + " and is now OVERDUE. Please return it as soon as possible.";

        sendNotification(tx.getUser().getId(), "Book Overdue", message);
    }

}

