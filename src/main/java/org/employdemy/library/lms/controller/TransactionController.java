package org.employdemy.library.lms.controller;

import org.employdemy.library.lms.dto.ApiResponse;
import org.employdemy.library.lms.dto.TransactionRequestDTO;
import org.employdemy.library.lms.dto.TransactionResponseDTO;
import org.employdemy.library.lms.model.TransactionStatus;
import org.employdemy.library.lms.service.TransactionService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<?>> approveBorrow(@PathVariable Long transactionId) {

        try{
            transactionService.approveBorrow(transactionId);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            200,
                            "Borrow Approved",
                            null
                    )
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(
                            400,
                            "Failed to approve borrow request",
                            e.getMessage()
                    )
            );
        }

    }

    // --------------------------------------------------------------
    // 1. DECLINE BORROW REQUEST
    // --------------------------------------------------------------
    @PostMapping("/decline/borrow/{transactionId}")
    public ResponseEntity<ApiResponse<?>> declineBorrow(
            @PathVariable Long transactionId
            , @RequestBody(required = false) String reason) {

        String message=transactionService.declineBorrow(transactionId,reason);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        200,
                        "Borrow Declined Successfully",
                        message
                )
        );
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
    public ResponseEntity<ApiResponse<?>> approveReturn(@PathVariable Long transactionId) {

        try{
            String message=transactionService.approveReturn(transactionId);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            200,
                            message,
                            null
                    )
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(
                            400,
                            "Failed to approve return request",
                            e.getMessage()
                    )
            );
        }
    }

    // --------------------------------------------------------------
    // 1. DECLINE RETURN REQUEST
    // --------------------------------------------------------------
    @PostMapping("/decline/return/{transactionId}")
    public ResponseEntity<ApiResponse<String>> declineReturn(
            @PathVariable Long transactionId,
            @RequestParam(required = false) String reason) {

        String message=transactionService.declineReturn(transactionId, reason);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Return Declined Successfully",
                        message
                )
        );
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
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> getAllBorrowedBooks() {
        try {
            return ResponseEntity.ok(transactionService.getAllTransactions());
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
    public ResponseEntity<ApiResponse<?>> sendReminder(@PathVariable Long transactionId) {

        String message =transactionService.sendReminder(transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        message,
                        null
                )
        );
    }

}

