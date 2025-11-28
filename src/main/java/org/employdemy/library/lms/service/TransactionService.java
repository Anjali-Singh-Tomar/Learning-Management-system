package org.employdemy.library.lms.service;

import org.employdemy.library.lms.dto.DueSoonResponseDTO;
import org.employdemy.library.lms.dto.TransactionRequestDTO;
import org.employdemy.library.lms.dto.TransactionResponseDTO;
import org.employdemy.library.lms.exception.ResourceAlreadyExistsException;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.mapper.TransactionMapper;
import org.employdemy.library.lms.model.*;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository,
                              BookService bookService, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
        this.transactionMapper = transactionMapper;
    }

    // ---------------------------------------------------------------------
    // BORROW BOOK
    // ---------------------------------------------------------------------
    public TransactionResponseDTO borrowBook(TransactionRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        Book book = bookService.getBookEntity(dto.getBookId());

        if (book.getAvailableCopies() <= 0)
            throw new ResourceNotFoundException("Book is not available to borrow");

        transactionRepository.findByUserAndBookAndStatus(user, book, TransactionStatus.BORROWED)
                .ifPresent(t -> {
                    throw new ResourceAlreadyExistsException("User already borrowed this book");
                });

        Transaction transaction = transactionMapper.toEntity(dto, user, book);

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    // ---------------------------------------------------------------------
    // RETURN BOOK
    // ---------------------------------------------------------------------
    public TransactionResponseDTO returnBook(TransactionRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        Book book = bookService.getBookEntity(dto.getBookId());

        Transaction transaction = transactionRepository
                .findByUserAndBookAndStatus(user, book, TransactionStatus.BORROWED)
                .orElseThrow(() -> new ResourceNotFoundException("No active borrow record found"));

        transaction.setReturnedAt(LocalDate.now());
        transaction.setStatus(TransactionStatus.RETURNED);

        book.setAvailableCopies(book.getAvailableCopies() + 1);

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    // ---------------------------------------------------------------------
    // RENEW BOOK
    // ---------------------------------------------------------------------
    public TransactionResponseDTO renewBook(TransactionRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        Book book = bookService.getBookEntity(dto.getBookId());

        Transaction transaction = transactionRepository
                .findByUserAndBookAndStatus(user, book, TransactionStatus.BORROWED)
                .orElseThrow(() -> new ResourceNotFoundException("No active borrow record found"));

        transaction.setDueDate(transaction.getDueDate().plusDays(14));
        transaction.setStatus(TransactionStatus.RENEWED);

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    // ---------------------------------------------------------------------
    // GET USER TRANSACTIONS
    // ---------------------------------------------------------------------
    public List<TransactionResponseDTO> getTransactionsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        return transactionRepository.findByUser(user).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
