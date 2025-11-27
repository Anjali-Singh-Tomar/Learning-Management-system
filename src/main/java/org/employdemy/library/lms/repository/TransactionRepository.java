package org.employdemy.library.lms.repository;

import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.dueDate < CURRENT_DATE
            AND t.returnedAt IS NULL
            ORDER BY t.dueDate ASC
           """)
    List<Transaction> findOverdueTransactions();

    long countByStatus(TransactionStatus status);

    @Query ("SELECT COUNT(t) FROM Transaction t WHERE t.dueDate < CURRENT_DATE AND t.returnedAt IS NULL")
    long countOverdueBooks();

    List<Transaction> findByUser(User user);

    // FIND ACTIVE BORROW
    Optional<Transaction> findByUserAndBookAndStatus(User user, Book book, TransactionStatus status);
}
