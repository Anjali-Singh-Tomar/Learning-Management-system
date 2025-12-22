package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.TransactionStatus;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibrarianService {

    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    public LibrarianOverviewResponse getLibrarianOverview(){

        LocalDate today=LocalDate.now();

        long todayIssued=transactionRepository.countByStatusAndBorrowedAt(TransactionStatus.BORROWED,today);
        long todayReturned=transactionRepository.countByStatusAndReturnedAt(TransactionStatus.RETURNED,today);
        long dueToday=transactionRepository.countByReturnedAtIsNullAndDueDate(today);
        long activeMembers=userRepository.countByActiveTrue();

        return new LibrarianOverviewResponse(
                todayIssued,
                todayReturned,
                dueToday,
                activeMembers
        );
    }

    @Transactional
    public LTAResponse getTodayActivity() {

        LocalDate today = LocalDate.now();

        List<IssuedTodayDTO> issuedToday =
                transactionRepository.findIssuedToday(today)
                        .stream()
                        .map(t -> new IssuedTodayDTO(
                                t.getId(),
                                t.getBook().getTitle(),
                                t.getUser().getName(),
                                t.getBorrowedAt()
                        ))
                        .toList();

        List<ReturnedTodayDTO> returnedToday =
                transactionRepository.findReturnedToday(today)
                        .stream()
                        .map(t -> new ReturnedTodayDTO(
                                t.getId(),
                                t.getBook().getTitle(),
                                t.getUser().getName(),
                                t.getReturnedAt()
                        ))
                        .toList();

        return new LTAResponse(
                issuedToday,
                returnedToday
        );
    }

    @Transactional
    public List<PendingReturnDTO> getPendingReturns() {

        LocalDate today = LocalDate.now();
        LocalDate sevenDays = today.plusDays(7);

        return transactionRepository.findPendingReturns(today, sevenDays)
                .stream()
                .map(t -> {

                    long daysLeft = ChronoUnit.DAYS.between(today, t.getDueDate());

                    String dueLabel;
                    if (daysLeft == 0) {
                        dueLabel = "Today";
                    } else if (daysLeft == 1) {
                        dueLabel = "Tomorrow";
                    } else {
                        dueLabel = "In " + daysLeft + " days";
                    }

                    return new PendingReturnDTO(
                            t.getId(),
                            t.getUser().getName(),
                            t.getBook().getTitle(),
                            t.getDueDate(),
                            dueLabel
                    );
                })
                .toList();
    }

    @Transactional
    public IssueBookResponseDTO issueBook(IssueBookRequestDTO dto){
        User user =userRepository.findById(dto.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("user not found: "+dto.getUserId()));

        if(!user.isActive()){
            throw new RuntimeException("User is not Active");
        }

        Book book=bookRepository.findById(dto.getBookId())
                .orElseThrow(()-> new ResourceNotFoundException("Book not foung: "+dto.getBookId()));

        if(book.getAvailableCopies()<=0){
            throw new RuntimeException("No Copies Available for this book");
        }

        boolean alreadyBorrowed= transactionRepository
                .existsByUserAndBookAndReturnedAtIsNull(user,book);

        if(alreadyBorrowed){
            throw new RuntimeException("User already borrowed this book");
        }

        Transaction transaction=new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setBorrowedAt(LocalDate.now());
        transaction.setDueDate(dto.getDueDate());
        transaction.setStatus(TransactionStatus.BORROWED);

        book.setAvailableCopies(book.getAvailableCopies()-1);

        transactionRepository.save(transaction);
        bookRepository.save(book);

        notificationService.sendNotification(
                transaction.getUser().getId(),
                "Book Issued",
                "Your Book "+transaction.getBook().getTitle()+"has been successfully issued by the Librarian"
        );

        return new IssueBookResponseDTO(
                transaction.getId(),
                user.getName(),
                book.getTitle(),
                dto.getDueDate(),
                "Book Issued Successfully"
        );
    }



}
