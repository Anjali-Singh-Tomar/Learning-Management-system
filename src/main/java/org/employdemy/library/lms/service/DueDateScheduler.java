package org.employdemy.library.lms.service;

import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.TransactionStatus;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DueDateScheduler {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;


    // Runs every day at 9 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDueSoonNotifications() {
        LocalDate today = LocalDate.now();
        LocalDate dueSoonDate = today.plusDays(3);

        List<Transaction> dueSoonTransactions =
                transactionRepository.findByStatusAndDueDate(TransactionStatus.BORROWED, dueSoonDate);

        // send notification to admin and librarian as a list
        notificationService.sendDueSoonReminderToAdminLib(dueSoonTransactions);

        // send notification to each member
        dueSoonTransactions.forEach(tx -> {
            notificationService.sendDueSoonReminder(tx);
        });
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void sendOverdueNotifications() {
        LocalDate today = LocalDate.now();

        List<Transaction> overdue = transactionRepository
                .findByStatusAndDueDateBefore(TransactionStatus.BORROWED, today);

        // send notification to admin and librarian as a list
        notificationService.sendOverdueReminderToAdminLib(overdue);

        // send notification to each member
        overdue.forEach(tx -> {
            notificationService.sendOverdueReminder(tx);
        });
    }

}

