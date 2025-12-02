package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.AdminOverviewResponse;
import org.employdemy.library.lms.dto.DueSoonResponseDTO;
import org.employdemy.library.lms.dto.OverdueRecordDTO;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.TransactionStatus;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AdminOverviewResponse getAdminOverview() {

        long totalBooks = bookRepository.count();
        long borrowedBooks = transactionRepository.countByStatus(TransactionStatus.BORROWED);
        long overdueBooks = transactionRepository.countByStatusAndDueDateBefore(TransactionStatus.BORROWED, LocalDate.now());
        long activeUsers = userRepository.countByActiveTrue();

        return new AdminOverviewResponse(
                totalBooks,
                borrowedBooks,
                overdueBooks,
                activeUsers
        );
    }

    public List<OverdueRecordDTO> getOverdueBooks() {

        List<Transaction> transactions = transactionRepository.findOverdueTransactions();

        return transactions.stream().map(t -> {
            OverdueRecordDTO dto = new OverdueRecordDTO();
            dto.setTransactionId(t.getId());
            dto.setBookTitle(t.getBook().getTitle());
            dto.setIsbn(t.getBook().getIsbn());
            dto.setBorrowerName(t.getUser().getName());
            dto.setBorrowerEmail(t.getUser().getEmail());
            dto.setDueDate(t.getDueDate().toString());

            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(t.getDueDate(), java.time.LocalDate.now());
            dto.setDaysOverdue(daysOverdue);

            return dto;
        }).toList();
    }

    public List<DueSoonResponseDTO> getDueSoonTransactions(int days) {

        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(days);

        List<Transaction> transactions = transactionRepository
                .findByStatusAndDueDateBetween(TransactionStatus.BORROWED, today, limit);

        return transactions.stream().map(tx -> {
            DueSoonResponseDTO dto = new DueSoonResponseDTO();
            dto.setBorrowId(tx.getId());
            dto.setMemberName(tx.getUser().getName());
            dto.setBookTitle(tx.getBook().getTitle());
            dto.setIssuedDate(tx.getBorrowedAt());
            dto.setDueDate(tx.getDueDate());
            dto.setDaysRemaining(java.time.temporal.ChronoUnit.DAYS.between(today, tx.getDueDate()));
            return dto;
        }).toList();
    }



    public List<Transaction> getBorrowedBooks(Long memberId){
        return transactionRepository.findCurrentBorrowed(memberId);
    }


    public List<Book> getRecommendedBooks(Long memberId){
        return transactionRepository.findRecommendedBooks(memberId);
    }


}

