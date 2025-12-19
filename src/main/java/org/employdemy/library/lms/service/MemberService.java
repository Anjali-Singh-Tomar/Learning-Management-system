package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.BorrowedBookDTO;
import org.employdemy.library.lms.dto.MemberDashboardResponseDTO;
import org.employdemy.library.lms.dto.MyBooksDTO;
import org.employdemy.library.lms.dto.RecommendedBookDTO;
import org.employdemy.library.lms.model.Transaction;
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
public class MemberService {

    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public List<MyBooksDTO> getMyBooks(Long memberId){
        List<Transaction> list=transactionRepository.findCurrentBorrowed(memberId);

        return list.stream().map(t -> {
            LocalDate today=LocalDate.now();
            int daysLeft=(int) today.until(t.getDueDate()).getDays();


            return new MyBooksDTO(
                    t.getId(),
                    t.getBook().getId(),
                    t.getBook().getTitle(),
                    t.getBook().getAuthor(),
                    t.getBorrowedAt().toString(),
                    t.getDueDate().toString(),
                    daysLeft,
                    transactionRepository.findStatus(t.getId()),
                    t.getBook().getImageData() != null
                            ? Base64.getEncoder().encodeToString(t.getBook().getImageData())
                            : null

            );
        }).toList();

    }
}

