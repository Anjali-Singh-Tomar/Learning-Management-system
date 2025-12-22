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
    // 1. REQUEST BORROW
    // --------------------------------------------------------------
    @PostMapping("/borrow")
    public ResponseEntity<?> requestBorrow(@Valid @RequestBody TransactionRequestDTO dto) {
        try {
            return ResponseEntity.ok(transactionService.createBorrowRequest(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // --------------------------------------------------------------
    // 1. APPROVE BORROW REQUEST
    // --------------------------------------------------------------
    @PostMapping("/approve/borrow/{transactionId}")
    public ResponseEntity<?> approveBorrow(@PathVariable Long transactionId) {
        try {
            return ResponseEntity.ok(transactionService.approveBorrow(transactionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --------------------------------------------------------------
    // 1. DECLINE BORROW REQUEST
    // --------------------------------------------------------------
    @PostMapping("/decline/borrow/{transactionId}")
    public ResponseEntity<?> declineBorrow(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.declineBorrow(transactionId));
    }

    // --------------------------------------------------------------
    // 2. RETURN - REQUEST
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
    // 1. APPROVE RETURN REQUEST
    // --------------------------------------------------------------
    @PostMapping("/approve/return/{transactionId}")
    public ResponseEntity<?> approveReturn(@PathVariable Long transactionId) {
        try {
            return ResponseEntity.ok(transactionService.approveReturn(transactionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --------------------------------------------------------------
    // 1. DECLINE RETURN REQUEST
    // --------------------------------------------------------------
    @PostMapping("/decline/return/{transactionId}")
    public ResponseEntity<?> declineReturn(
            @PathVariable Long transactionId,
            @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(transactionService.declineReturn(transactionId, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // --------------------------------------------------------------
    // 3. RENEW BOOK
    // --------------------------------------------------------------
    @PostMapping("/renew/{transactionId}")
    public ResponseEntity<?> renewBook(@PathVariable Long transactionId) {
        try {
            return ResponseEntity.ok(transactionService.createRenewRequest(transactionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // --------------------------------------------------------------
    // 3. APPROVE - RENEW BOOK
    // --------------------------------------------------------------
    @PostMapping("/approve/renew/{transactionId}")
    public ResponseEntity<?> renewApprove(@PathVariable Long transactionId) {
        try {
            return ResponseEntity.ok(transactionService.approveRenewRequest(transactionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // --------------------------------------------------------------
    // 1. DECLINE RENEW REQUEST
    // --------------------------------------------------------------
    @PostMapping("/decline/renew/{transactionId}")
    public ResponseEntity<?> declineRenew(
            @PathVariable Long transactionId,
            @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(transactionService.declineRenew(transactionId, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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

    // --------------------------------------------------------------
    // 6. SEND REMINDER TO MEMBER FOR RETURN
    // --------------------------------------------------------------
    @PostMapping("/{transactionId}/reminder")
    public ResponseEntity<?> sendReminder(@PathVariable Long transactionId) {

        transactionService.sendReminder(transactionId);
        return ResponseEntity.ok(Map.of(
                "message", "Reminder sent successfully"
        ));
    }

}

