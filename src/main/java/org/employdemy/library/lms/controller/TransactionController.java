package org.employdemy.library.lms.controller;

import org.employdemy.library.lms.dto.TransactionRequestDTO;
import org.employdemy.library.lms.dto.TransactionResponseDTO;
import org.employdemy.library.lms.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // BORROW BOOK
    @PostMapping("/borrow")
    public ResponseEntity<TransactionResponseDTO> borrowBook(@Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.borrowBook(dto));
    }

    // RETURN BOOK
    @PostMapping("/return")
    public ResponseEntity<TransactionResponseDTO> returnBook(@Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.returnBook(dto));
    }

    // RENEW BOOK
    @PostMapping("/renew")
    public ResponseEntity<TransactionResponseDTO> renewBook(@Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.renewBook(dto));
    }

    // GET USER TRANSACTIONS
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userId));
    }
}
