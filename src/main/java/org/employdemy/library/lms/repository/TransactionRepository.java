package org.employdemy.library.lms.repository;

import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    // FIND ACTIVE BORROW
    Optional<Transaction> findByUserAndBookAndStatus(User user, Book book, TransactionStatus status);


    long countByStatus(TransactionStatus status);

    long countByStatusAndDueDateBefore(TransactionStatus status, LocalDate date);


    // 1. Current borrowed books (not returned)
    long countByUser_IdAndReturnedAtIsNull(Long userId);

    // 2. Books user has already read (returned)
    long countByUser_IdAndReturnedAtIsNotNull(Long userId);

    // 3. Items due soon (next 5 days)
    @Query("""
        SELECT COUNT(t)
        FROM Transaction t
        WHERE t.user.id = :userId
          AND t.returnedAt IS NULL
          AND t.dueDate BETWEEN :today AND :fiveDays
    """)
    long countDueSoon(Long userId, LocalDate today, LocalDate fiveDays);

    // 4. List of currently borrowed books
    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.user.id = :userId
      AND t.status IN ('BORROWED', 'RENEWED')
""")
    List<Transaction> findCurrentBorrowed(Long userId);

    // History of user transaction
    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.user.id = :userId
    """)
    List<Transaction> findTransactionHistory(Long userId);

    //find the overdue list
    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.dueDate < CURRENT_DATE
            AND t.returnedAt IS NULL
            ORDER BY t.dueDate ASC
           """)
    List<Transaction> findOverdueTransactions();

    //find due in a week
    List<Transaction> findByStatusAndDueDateBetween(
            TransactionStatus status,
            LocalDate start,
            LocalDate end
    );

    //fetch by given status
    List<Transaction> findByStatus(TransactionStatus status);

    //search from the list of borrowed books on based on either username or book title -->edit for
    @Query("""
    SELECT t FROM Transaction t
    JOIN t.user u
    JOIN t.book b
    WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
         OR LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')))
""")
    List<Transaction> searchBorrowedBooks(@Param("query") String query);

    //COUNT THE NUMBER OF BOOKS BORROWED BY THE USER IN
    @Query("""
    SELECT COUNT(t)
    FROM Transaction t
    WHERE t.user.id = :userId
""")
    int countAllBorrowedBooks(@Param("userId") Long userId);

    List<Transaction> findByStatusAndDueDate(TransactionStatus status, LocalDate dueDate);

    List<Transaction> findByStatusAndDueDateBefore(TransactionStatus status, LocalDate date);


    //Librarian Dashboard
    long countByStatusAndBorrowedAt(TransactionStatus status, LocalDate date);

    long countByStatusAndReturnedAt(TransactionStatus status, LocalDate date);

    long countByReturnedAtIsNullAndDueDate(LocalDate date);

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.status = 'BORROWED'
      AND t.borrowedAt = :today
""")
    List<Transaction> findIssuedToday(LocalDate today);


    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.status = 'RETURNED'
      AND t.returnedAt = :today
""")
    List<Transaction> findReturnedToday(LocalDate today);


    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.returnedAt IS NULL
      AND t.dueDate BETWEEN :today AND :sevenDays
    ORDER BY t.dueDate ASC
""")
    List<Transaction> findPendingReturns(
            LocalDate today,
            LocalDate sevenDays
    );


    @Query("""
            SELECT status
            FROM Transaction t
            WHERE t.id = :transactionId
""")
    String findStatus(Long transactionId);



    boolean existsByUserAndBookAndReturnedAtIsNull(User user, Book book);


}
