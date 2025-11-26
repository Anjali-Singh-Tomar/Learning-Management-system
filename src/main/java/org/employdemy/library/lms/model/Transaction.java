package org.employdemy.library.lms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many transactions belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Many transactions belong to one book
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDate borrowedAt;
    private LocalDate dueDate;
    private LocalDate returnedAt;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // BORROWED, RETURNED, RENEWED
}
