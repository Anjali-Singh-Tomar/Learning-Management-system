package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.dto.ApiResponse;
import org.employdemy.library.lms.dto.IssueBookRequestDTO;
import org.employdemy.library.lms.dto.LibrarianOverviewResponse;
import org.employdemy.library.lms.service.LibrarianService;
import org.employdemy.library.lms.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/librarian")
@RequiredArgsConstructor
@CrossOrigin
public class LibrarianController {

    private final LibrarianService librarianService;
    private final TransactionService transactionService;

    @GetMapping("/dashboard/overview")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> getLibrarianOverview(){
        try{
            return ResponseEntity.ok(librarianService.getLibrarianOverview());
        }catch (Exception e){
           return ResponseEntity.status(500).body(Map.of("error","Failed to load Librarian Overview"));
        }
    }

    @GetMapping("/dashboard/today-activity")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> getTodayActivity() {
        try {
            return ResponseEntity.ok(librarianService.getTodayActivity());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard/pending-returns")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> getPendingReturns() {
        try {
            var data = librarianService.getPendingReturns();

            if (data.isEmpty()) {
                return ResponseEntity.ok(
                        Map.of("message", "No pending returns")
                );
            }

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to load pending returns"));
        }
    }

    @PostMapping("/sidebar/issuebook")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> issueBook(@RequestBody IssueBookRequestDTO dto){
        try {
            return ResponseEntity.ok(librarianService.issueBook(dto));
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error",e.getMessage()));
        }
    }

    @GetMapping("/sidebar/managebooks")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> manageBooks(){
        try {
            return ResponseEntity.ok(librarianService.getManageBooks());
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error",e.getMessage()));
        }
    }

    @GetMapping("/sidebar/managemembers")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> manageMembers(){
        try{
            return ResponseEntity.ok(librarianService.manageMembers());
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error",e.getMessage()));
        }
    }

    @GetMapping("/sidebar/pending-request")
    public ResponseEntity<ApiResponse<?>> pendingRequest(){

        try{
            List<PendingResponseDTO> data=transactionService.getAllRequests();

            return ResponseEntity.ok(
                    ApiResponse.success(
                            200,
                            "Request data fetched",
                            data
                    )
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(
                            400,
                            "Failed to fetch the data"+e.getMessage(),
                            null
                    )
            );
        }
    }

    @GetMapping("/sidebar/borrowedBooks")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<?>> borrowedBooks(){
        try{
            return ResponseEntity.ok(
                    ApiResponse.success(
                            200,
                            "Borrowed Books fetched Successfully",
                            librarianService.getBorrowedBooks()
                    )
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(
                            500,
                            "Failed to Fetch the Borrowed Books",
                            e.getMessage()
                    )
            );
        }
    }

}
