package org.employdemy.library.lms.service;

import lombok.AllArgsConstructor;
import org.employdemy.library.lms.dto.DueSoonResponseDTO;
import org.employdemy.library.lms.dto.TransactionRequestDTO;
import org.employdemy.library.lms.dto.TransactionResponseDTO;
import org.employdemy.library.lms.exception.ResourceAlreadyExistsException;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.mapper.TransactionMapper;
import org.employdemy.library.lms.model.*;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;


    // HELPER METHOD FOR SEND-REMINDER
    private String buildSingleReminderMessage(Transaction tx) {

        return "Dear " + tx.getUser().getName() + ",\n\n" +
                "This is a reminder to return the borrowed book:\n\n" +
                "Book Title: " + tx.getBook().getTitle() + "\n" +
                "Due Date: " + tx.getDueDate() + "\n\n" +
                "Please return or renew the book at the earliest.\n\n" +
                "Library Management System";
    }


    // ---------------------------------------------------------------------
    // RETURN BOOK- REQUEST
    // ---------------------------------------------------------------------
    @Transactional
    public String returnBook(TransactionRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        Book book = bookService.getBookEntity(dto.getBookId());
        Transaction tx = transactionRepository
                .findByUserAndBookAndStatus(user, book, TransactionStatus.BORROWED)
                .orElseThrow(() -> new RuntimeException("No active borrowed transaction found."));

        // Create a return request
        tx.setStatus(TransactionStatus.RETURN_REQUESTED);

        transactionRepository.save(tx);

        // Send notification to librarians
        List<User> librarians = userRepository.findByRole(Role.LIBRARIAN);
        for (User librarian : librarians) {
            notificationService.sendNotification(
                    librarian.getId(),
                    "Return Request Pending",
                    "User '" + tx.getUser().getName() +
                            "' has requested to return the book '" + tx.getBook().getTitle() + "'."
            );
        }

        return "Return request submitted successfully.";
    }


    // ---------------------------------------------------------------------
    // RETURN REQUEST APPROVED
    // ---------------------------------------------------------------------
    @Transactional
    public String approveReturn(Long transactionId) {

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getStatus() != TransactionStatus.RETURN_REQUESTED) {
            throw new RuntimeException("This is not a return request.");
        }

        tx.setReturnedAt(LocalDate.now());
        tx.setStatus(TransactionStatus.RETURNED);
        transactionRepository.save(tx);

        // Notify user
        notificationService.sendNotification(
                tx.getUser().getId(),
                "Return Approved",
                "Your return request for '" + tx.getBook().getTitle() + "' has been approved."
        );

        return "Return successfully completed.";
    }


    // ---------------------------------------------------------------------
    // RETURN REQUEST DECLINE
    // ---------------------------------------------------------------------
    @Transactional
    public String declineReturn(Long transactionId, String reason) {

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getStatus() != TransactionStatus.RETURN_REQUESTED) {
            throw new RuntimeException("This is not a return request.");
        }

        tx.setStatus(TransactionStatus.RETURN_DECLINED);
        transactionRepository.save(tx);

        notificationService.sendNotification(
                tx.getUser().getId(),
                "Return Declined",
                "Your request to return '" + tx.getBook().getTitle() + "' was declined. " +
                        (reason != null ? "Reason: " + reason : "")
        );

        return "Return request declined.";
    }


    // ---------------------------------------------------------------------
    // RENEW BOOK
    // ---------------------------------------------------------------------
    public TransactionResponseDTO renewBook(TransactionRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        Book book = bookService.getBookEntity(dto.getBookId());
        Transaction tx = transactionRepository
                .findByUserAndBookAndStatus(user, book, TransactionStatus.BORROWED)
                .orElseThrow(() -> new RuntimeException("Cannot Renew Book"));

        // Send notification to librarians
        List<User> librarians = userRepository.findByRole(Role.LIBRARIAN);
        for (User librarian : librarians) {
            notificationService.sendNotification(
                    librarian.getId(),
                    "Renew Book",
                    user.getName() + " has Renewed the book " + book.getTitle()
            );
        }

        tx.setStatus(TransactionStatus.RENEWED);
        Transaction saved = transactionRepository.save(tx);
        return transactionMapper.toDTO(saved);
    }


    // ---------------------------------------------------------------------
    // GET USER TRANSACTIONS-NOT USING
    // ---------------------------------------------------------------------
//    public List<TransactionResponseDTO> getTransactionsByUser(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
//
//        return transactionRepository.findByUser(user).stream()
//                .map(transactionMapper::toDTO)
//                .collect(Collectors.toList());
//    }


    // ---------------------------------------------------------------------
    // GET ALL THE BORROWED BOOKS
    // ---------------------------------------------------------------------
    public List<TransactionResponseDTO> getAllBorrowedBooks() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    // ---------------------------------------------------------------------
    // SEARCH BORROWED BOOKS
    // ---------------------------------------------------------------------
    public List<TransactionResponseDTO> searchBorrowedBooks(String query) {

        return transactionRepository.searchBorrowedBooks(query)
                .stream()
                .map(t -> new TransactionResponseDTO(
                        t.getId(),
                        t.getUser().getId(),
                        t.getUser().getName(),
                        t.getBook().getId(),
                        t.getBook().getTitle(),
                        t.getBorrowedAt(),
                        t.getDueDate(),
                        t.getReturnedAt(),
                        t.getRequestedAt(),
                        t.getStatus()
                )).toList();
    }

    public List<TransactionResponseDTO> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    // ---------------------------------------------------------------------
    // BORROW REQUEST TO LIBRARIAN
    // ---------------------------------------------------------------------
    public TransactionResponseDTO createBorrowRequest(TransactionRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Create pending transaction
        Transaction tx = transactionMapper.toEntity(dto,user,book);

        Transaction saved = transactionRepository.save(tx);

        // Send notification to librarians
        List<User> librarians = userRepository.findByRole(Role.LIBRARIAN);
        for (User librarian : librarians) {
            notificationService.sendNotification(
                    librarian.getId(),
                    "Borrow Request",
                    user.getName() + " has requested to borrow " + book.getTitle()
            );
        }

        return transactionMapper.toDTO(saved);
    }


    // ---------------------------------------------------------------------
    // APPROVE BORROW REQUEST
    // ---------------------------------------------------------------------
    public TransactionResponseDTO approveBorrow(Long id) {

        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        tx.setStatus(TransactionStatus.BORROWED);
        tx.setBorrowedAt(LocalDate.now());
        tx.setDueDate(java.time.LocalDate.now().plusDays(14));
        tx.getBook().setAvailableCopies(tx.getBook().getAvailableCopies() - 1);

        Transaction saved = transactionRepository.save(tx);

        // Notify the user
        notificationService.sendNotification(
                tx.getUser().getId(),
                "Borrow Approved",
                "Your request for '" + tx.getBook().getTitle() + "' has been approved"
        );

        return transactionMapper.toDTO(saved);
    }


    // ---------------------------------------------------------------------
    // DECLINE BORROW REQUEST
    // ---------------------------------------------------------------------
    @Transactional
    public String declineBorrow(Long transactionId) {

        // Fetch transaction
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Validate status
        if (tx.getStatus() != TransactionStatus.REQUESTED) {
            throw new RuntimeException("Only borrow requests can be declined.");
        }

        // Update transaction status
        tx.setStatus(TransactionStatus.DECLINED);
        transactionRepository.save(tx);

        // Notify the user
        notificationService.sendNotification(
                tx.getUser().getId(),
                "Borrow Request Declined",
                "Your borrow request for '" + tx.getBook().getTitle() +
                        "' was declined."
        );

        return "Borrow request declined successfully.";
    }

    public void sendReminder(Long transactionId) {

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getStatus() != TransactionStatus.BORROWED) {
            throw new RuntimeException("Reminder can only be sent for borrowed books");
        }

        User member = tx.getUser();

        String title = "📚 Book Return Reminder";
        String message = buildSingleReminderMessage(tx);

        // Notify member
        notificationService.sendNotification(
                member.getId(),
                title,
                message
        );

        // OPTIONAL: notify admin
//        notifyAdminForManualReminder(tx);
    }



}
