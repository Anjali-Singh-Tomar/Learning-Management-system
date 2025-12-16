package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.model.TransactionStatus;
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



}
