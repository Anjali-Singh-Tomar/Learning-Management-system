package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
//import org.employdemy.library.lms.dto.AdminOverviewResponseDTO;
import org.employdemy.library.lms.dto.AdminOverviewResponse;
import org.employdemy.library.lms.dto.BorrowedBookDTO;
import org.employdemy.library.lms.dto.MemberDashboardResponseDTO;
import org.employdemy.library.lms.dto.RecommendedBookDTO;
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
        long overdueBooks = transactionRepository.countByStatusAndDueDateBefore(TransactionStatus.BORROWED,LocalDate.now());
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

    public List<BorrowedBookDTO> getBorrowedBooks(Long memberId) {

        return transactionRepository.findCurrentBorrowed(memberId)
                .stream()
                .map(t -> {
                    BorrowedBookDTO dto = new BorrowedBookDTO();
                    dto.setTransactionId(t.getId());
                    dto.setBookId(t.getBook().getId());
                    dto.setTitle(t.getBook().getTitle());
                    dto.setAuthor(t.getBook().getAuthor());
                    dto.setBorrowedAt(t.getBorrowedAt());
                    dto.setDueDate(t.getDueDate());
                    return dto;
                })
                .toList();
    }


    public List<RecommendedBookDTO> getRecommendedBooks(Long memberId) {

        return transactionRepository.findRecommendedBooks(memberId)
                .stream()
                .map(book -> {
                    RecommendedBookDTO dto = new RecommendedBookDTO();
                    dto.setId(book.getId());
                    dto.setTitle(book.getTitle());
                    dto.setAuthor(book.getAuthor());
                    dto.setGenre(book.getGenre() != null ? book.getGenre().name() : null);
                    dto.setPublishedYear(book.getPublishedYear());

                    // Image mapping
                    dto.setImageName(book.getImageName());
                    dto.setImageType(book.getImageType());
                    if (book.getImageData() != null) {
                        dto.setImageBase64(
                                java.util.Base64.getEncoder().encodeToString(book.getImageData())
                        );
                    }

                    return dto;
                })
                .toList();
    }



}

