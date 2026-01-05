package org.employdemy.library.lms.mapper;

import org.employdemy.library.lms.dto.PendingResponseDTO;
import org.employdemy.library.lms.dto.TransactionRequestDTO;
import org.employdemy.library.lms.dto.TransactionResponseDTO;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.TransactionStatus;
import org.employdemy.library.lms.model.User;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUser().getId());
        dto.setUserName(transaction.getUser().getName());
        dto.setBookId(transaction.getBook().getId());
        dto.setBookTitle(transaction.getBook().getTitle());
        dto.setBorrowedAt(transaction.getBorrowedAt());
        dto.setDueDate(transaction.getDueDate());
        dto.setReturnedAt(transaction.getReturnedAt());
        dto.setRequestedAt(transaction.getRequestedAt());
        dto.setStatus(transaction.getStatus());
        return dto;
    }

    public Transaction toEntity( User user, Book book) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setRequestedAt(java.time.LocalDateTime.now());
//        transaction.setDueDate(java.time.LocalDate.now().plusDays(14)); // default 2 weeks
        transaction.setStatus(TransactionStatus.REQUESTED);
        return transaction;
    }

    public PendingResponseDTO toPendingDTO(Transaction transaction) {

        PendingResponseDTO dto = new PendingResponseDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUser().getId());
        dto.setUserName(transaction.getUser().getName());
        dto.setBookId(transaction.getBook().getId());
        dto.setBookTitle(transaction.getBook().getTitle());
        dto.setBookISBN(transaction.getBook().getIsbn());
        dto.setDueDate(transaction.getDueDate());

        if(transaction.getStatus()== TransactionStatus.REQUESTED)
            dto.setType("issue");
        else if(transaction.getStatus()== TransactionStatus.RETURN_REQUESTED)
            dto.setType("return");

        return dto;
    }

}
