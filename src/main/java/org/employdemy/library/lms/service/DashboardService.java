package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
//import org.employdemy.library.lms.dto.AdminOverviewResponseDTO;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.model.Book;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Base64;
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

    public MemberDashboardResponseDTO getMemberOverview(Long memberId){
        long currentlyBorrowed= transactionRepository.countByUser_IdAndReturnedAtIsNull(memberId);
        long booksRead=transactionRepository.countByUser_IdAndReturnedAtIsNotNull(memberId);

        long dueSoon;
        if(currentlyBorrowed>0){
             dueSoon= transactionRepository.countDueSoon(memberId, LocalDate.now(),LocalDate.now().plusDays(5));
        } else dueSoon=0L;

        List<Transaction> transactions = transactionRepository.findOverdueTransactions();

        return new MemberDashboardResponseDTO(
                currentlyBorrowed,
                booksRead,
                dueSoon
        );
    }

    @Transactional
    public List<BorrowedBookDTO> getAllBooks(Long memberId) {

        return transactionRepository.findTransactionHistory(memberId)
                .stream()
                .map(t -> {
                    BorrowedBookDTO dto = new BorrowedBookDTO();
                    dto.setTransactionId(t.getId());
                    dto.setBookId(t.getBook().getId());
                    dto.setTitle(t.getBook().getTitle());
                    dto.setAuthor(t.getBook().getAuthor());
                    dto.setBorrowedAt(t.getBorrowedAt());
                    dto.setDueDate(t.getDueDate());
                    dto.setStatus(t.getStatus());
                    dto.setReturnedAt(t.getReturnedAt());
                    return dto;
                })
                .toList();
    }


    public List<RecommendedBookDTO> getRecommendedBooks(Long memberId) {

        // Fetch only IDs from JPQL — no LOBs touched
        List<Long> bookIds = bookRepository.findRecommendedBookIds(memberId);

        return bookIds.stream()
                .map(id -> bookRepository.findById(id).orElse(null))
                .filter(book -> book != null)
                .map(book -> new RecommendedBookDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getPublishedYear(),
                        book.getImageData() != null
                                ? Base64.getEncoder().encodeToString(book.getImageData())
                                : null,
                        book.getImageName(),
                        book.getImageType()
                ))
                .toList();
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

    @Transactional
    public List<MyBooksDTO> getMyBooks(Long memberId){
        List<Transaction> list=transactionRepository.findCurrentBorrowed(memberId);

        return list.stream().map(t -> {
            LocalDate today=LocalDate.now();
            int daysLeft=(int) today.until(t.getDueDate()).getDays();

            String status;
            if(daysLeft < 0) status = "OVERDUE";
            else if (daysLeft <= 5) {
                status="DUE_SOON";
            }
            else status = "SAFE";

            return new MyBooksDTO(
                    t.getId(),
                    t.getBook().getId(),
                    t.getBook().getTitle(),
                    t.getBook().getAuthor(),
                    t.getBorrowedAt().toString(),
                    t.getDueDate().toString(),
                    daysLeft,
                    status,
                    t.getBook().getImageData() != null
                            ? Base64.getEncoder().encodeToString(t.getBook().getImageData())
                            : null

            );
        }).toList();

    }

}

