package org.employdemy.library.lms.controller;

import org.employdemy.library.lms.dto.TransactionRequestDTO;
import org.employdemy.library.lms.dto.TransactionResponseDTO;
import org.employdemy.library.lms.model.TransactionStatus;
import org.employdemy.library.lms.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // --------------------------------------------------------------
    // 1. BORROW BOOK
    // --------------------------------------------------------------
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@Valid @RequestBody TransactionRequestDTO dto) {
        try {
            return ResponseEntity.ok(transactionService.borrowBook(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // --------------------------------------------------------------
    // 2. RETURN BOOK
    // --------------------------------------------------------------
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@Valid @RequestBody TransactionRequestDTO dto) {
        try {
            return ResponseEntity.ok(transactionService.returnBook(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // --------------------------------------------------------------
    // 3. RENEW BOOK
    // --------------------------------------------------------------
    @PostMapping("/renew")
    public ResponseEntity<?> renewBook(@Valid @RequestBody TransactionRequestDTO dto) {
        try {
            return ResponseEntity.ok(transactionService.renewBook(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // --------------------------------------------------------------
    // 4. GET ALL BORROWED BOOKS (Admin + Librarian)
    // --------------------------------------------------------------
    @GetMapping("/borrowed-books")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> getAllBorrowedBooks() {
        try {
            return ResponseEntity.ok(transactionService.getAllBorrowedBooks());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to fetch borrowed books"
            ));
        }
    }

    // --------------------------------------------------------------
    // 5. SEARCH BORROWED BOOKS
    // --------------------------------------------------------------
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> searchBorrowedBooks(@RequestParam String query) {
        try {
            List<TransactionResponseDTO> results = transactionService.searchBorrowedBooks(query);

            if (results.isEmpty())
                return ResponseEntity.ok(Map.of(
                        "message", "No records found for: " + query
                ));

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Search failed"
            ));
        }
    }

    // --------------------------------------------------------------
    // 6. FILTER TRANSACTIONS BY STATUS
    // --------------------------------------------------------------
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> filterByStatus(@RequestParam TransactionStatus status) {
        try {
            List<TransactionResponseDTO> results = transactionService.getTransactionsByStatus(status);

            if (results.isEmpty())
                return ResponseEntity.ok(Map.of(
                        "message", "No transactions found with status: " + status
                ));

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to filter transactions"
            ));
        }
    }
}

