package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.model.Genre;
import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.TransactionStatus;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {


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

    public List<AdminManageUsersDTO> getManageUsers(){

        List<User> users=userRepository.findAllUsers();

        return users.stream()
                .map(u->{
                    AdminManageUsersDTO dto=new AdminManageUsersDTO();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setJoinDate(u.getJoiningDate());
                    dto.setRole(u.getRole());
                    dto.setStatus(u.isActive()?"Active":"Not Active");
                    return dto;
                })
                .toList();
    }

    public List<ManageMembersDTO> getSearchMembers(String keyword){

        List<User> users=userRepository.searchAllUsers(keyword);

        return users.stream()
                .map(u->{
                    ManageMembersDTO dto=new ManageMembersDTO();
                    dto.setId(u.getId());
                    dto.setMemberName(u.getName());
                    dto.setMemberId(u.getEmpId());
                    dto.setMemberEmail(u.getEmail());
                    dto.setJoiningDate(u.getJoiningDate());
                    dto.setBorrowedCount(transactionRepository.countByUser_IdAndReturnedAtIsNull(u.getId()));
                    dto.setTotalBorrowed(transactionRepository.countByUser_IdAndReturnedAtIsNotNull(u.getId()));
                    dto.setOverdue(transactionRepository.countOverdueByUser(u.getId(),LocalDate.now()));
                    dto.setStatus(u.isActive()?"Active":"Not Active");
                    dto.setRole(u.getRole());

                    return dto;
                })
                .toList();
    }

    public List<ReportActivityDTO> getBorrowerGraph(){

            YearMonth endMonth = YearMonth.now().minusMonths(1); // last completed month
            YearMonth startMonth = endMonth.minusMonths(5);

            LocalDate fromDate = startMonth.atDay(1);

            List<Transaction> data =
                    transactionRepository.countBorrowedBooksFromDate(fromDate);

            Map<YearMonth, Integer> dbMap = new HashMap<>();
            for (Transaction t : data) {
                LocalDate borrowedAt = t.getBorrowedAt();
                if (borrowedAt == null) {
                    continue;
                }
                YearMonth yearMonth = YearMonth.from(borrowedAt);
                dbMap.put(
                        yearMonth,
                        dbMap.getOrDefault(yearMonth, 0) + 1
                );
            }

            List<ReportActivityDTO> result = new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                YearMonth ym = startMonth.plusMonths(i);
                int count = dbMap.getOrDefault(ym, 0);

                String label = ym.getMonth()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                result.add(new ReportActivityDTO(label, count));
            }
            return result;
    }

    public List<ReportActivityDTO> getMemberGraph(){

        YearMonth endMonth = YearMonth.now().minusMonths(1); // last completed month
        YearMonth startMonth = endMonth.minusMonths(5);

        LocalDate fromDate = startMonth.atDay(1);

        List<User> data =
               userRepository.findAllUsers() ;

        Map<YearMonth, Integer> dbMap = new HashMap<>();
        for (User t : data) {
            LocalDate joiningDate = t.getJoiningDate();
            if (joiningDate == null) {
                continue;
            }
            YearMonth yearMonth = YearMonth.from(joiningDate);
            dbMap.put(
                    yearMonth,
                    dbMap.getOrDefault(yearMonth, 0) + 1
            );
        }

        List<ReportActivityDTO> result = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            YearMonth ym = startMonth.plusMonths(i);
            int count = dbMap.getOrDefault(ym, 0);

            String label = ym.getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            result.add(new ReportActivityDTO(label, count));
        }
        return result;
    }


    public List<MonthlyActivityDTO> getBorrowReturnGraph(){

        YearMonth endMonth = YearMonth.now().minusMonths(1); // last completed month
        YearMonth startMonth = endMonth.minusMonths(5);

        LocalDate fromDate = startMonth.atDay(1);

        List<Transaction> data =
                transactionRepository.countBorrowedBooksFromDate(fromDate);

        Map<YearMonth, Integer> borrowedMap = new HashMap<>();
        Map<YearMonth, Integer> returnedMap = new HashMap<>();
        for (Transaction t : data) {
            if (t.getBorrowedAt() != null) {
                YearMonth borrowedMonth = YearMonth.from(t.getBorrowedAt());
                borrowedMap.put(
                        borrowedMonth,
                        borrowedMap.getOrDefault(borrowedMonth, 0) + 1
                );
            }

            if (t.getReturnedAt() != null) {
                YearMonth returnedMonth = YearMonth.from(t.getReturnedAt());
                returnedMap.put(
                        returnedMonth,
                        returnedMap.getOrDefault(returnedMonth, 0) + 1
                );
            }
        }

        List<MonthlyActivityDTO> result = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            YearMonth ym = startMonth.plusMonths(i);
            int borrowedCount = borrowedMap.getOrDefault(ym, 0);
            int returnedCount = returnedMap.getOrDefault(ym, 0);

            String label = ym.getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            result.add(new MonthlyActivityDTO(label, borrowedCount, returnedCount));
        }
        return result;
    }

    public List<BookCategoryDTO> getBookByCategory(){

        return bookRepository.findCountOfBooksByCategory()
                .stream()
                .map(row -> new BookCategoryDTO(
                        (Genre) row[0],
                        (Long) row[1]
                ))
                .toList();
    }

    public AdminReportsOverview getReportsOverview(){

        LocalDate startDate = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(1);

        LocalDate endDate = startDate
                .withDayOfMonth(startDate.lengthOfMonth());

        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);


        return new AdminReportsOverview(
                userRepository.countByActiveFalse(),
                transactionRepository.countBorrowedBooksLastMonth(startDate,endDate),
                transactionRepository.countMembersJoinedSinceLastMonth(lastMonthDate),
                bookRepository.countLowStockBooks()
        );
    }
}


