package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.model.*;
import org.employdemy.library.lms.repository.NotificationRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TransactionRepository transactionRepository;

    // get the list of librarian with admin
    private List<User> getAdminAndLibrarians() {

        List<User> recipients = new ArrayList<>();
        recipients.addAll(userRepository.findByRole(Role.ADMIN));
        recipients.addAll(userRepository.findByRole(Role.LIBRARIAN));

        return recipients;
    }

    // create a string of the transaction list
    private String buildMessage(List<Transaction> transactions) {

        StringBuilder sb = new StringBuilder();
        sb.append("The following books require attention:\n\n");
        sb.append("User ID | User Name | Book Title | Due Date\n");
        sb.append("------------------------------------------\n");

        for (Transaction tx : transactions) {
            sb.append(tx.getUser().getId()).append(" | ")
                    .append(tx.getUser().getName()).append(" | ")
                    .append(tx.getBook().getTitle()).append(" | ")
                    .append(tx.getDueDate())
                    .append("\n");
        }

        return sb.toString();
    }

    public void sendNotification(Long userId, String title, String message) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);

        notificationRepository.save(notification);

        // TODO: include if condition to filter the email that should be sent
        if (user.getEmail() != null) {
            emailService.sendEmail(
                    user.getEmail(),
                    title,
                    message
            );
        }
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
        sendNotification(tx.getUser().getId(), "Due Soon Books", message);
    }

    public void sendDueSoonReminderToAdminLib(List<Transaction> transactions) {

        if (transactions.isEmpty()) {
            return;
        }

        List<User> recipients = getAdminAndLibrarians();

        String title = "📘 Books Due Soon (Next 3 Days)";
        String message = buildMessage(transactions);

        for (User user : recipients) {
            sendNotification(
                    user.getId(),
                    title,
                    message
            );
        }
    }

    public void sendOverdueReminder(Transaction tx) {
        String message = "Your borrowed book '"
                + tx.getBook().getTitle()
                + "' was due on " + tx.getDueDate()
                + " and is now OVERDUE. Please return it as soon as possible.";

        sendNotification(tx.getUser().getId(), "Book Overdue", message);
    }

    public void sendOverdueReminderToAdminLib(List<Transaction> transactions) {

        if (transactions.isEmpty()) {
            return;
        }

        List<User> recipients = getAdminAndLibrarians();

        String title = "📘 Books Overdue for return";
        String message = buildMessage(transactions);

        for (User user : recipients) {
            sendNotification(
                    user.getId(),
                    title,
                    message
            );
        }
    }

}

