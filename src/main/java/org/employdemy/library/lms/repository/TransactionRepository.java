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

    //search from the list of borrowed books on based on either username or book title
    @Query("""
    SELECT t FROM Transaction t
    JOIN t.user u
    JOIN t.book b
    WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
         OR LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')))
""")
    List<Transaction> searchBorrowedBooks(@Param("query") String query);


}
