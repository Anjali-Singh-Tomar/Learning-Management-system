package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    // ==========================
    // ADMIN OVERVIEW
    // ==========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/overview")
    public ResponseEntity<?> getAdminOverview() {
        try {
            return ResponseEntity.ok(dashboardService.getAdminOverview());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load admin overview"));
        }
    }

    // ==========================
    // ADMIN OVERDUE BOOKS
    // ==========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/overdue")
    public ResponseEntity<?> getOverdueBooks() {
        try {
            return ResponseEntity.ok(dashboardService.getOverdueBooks());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch overdue books"));
        }
    }

    // ==========================
    // MEMBER OVERVIEW
    // ==========================
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member/{memberId}/overview")
    public ResponseEntity<?> getMemberOverview(@PathVariable Long memberId) {
        try {
            MemberDashboardResponseDTO dto = dashboardService.getMemberOverview(memberId);

            if (dto == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Member not found"));
            }
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch member overview"));
        }
    }

    // ==========================
    // MEMBER TRANSACTION HISTORY
    // ==========================
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member/{memberId}/history")
    public ResponseEntity<?> getBorrowedBooks(@PathVariable Long memberId) {
        try {
            List<BorrowedBookDTO> books = dashboardService.getAllBooks(memberId);

            if (books == null || books.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No borrowed books found"));
            }
            return ResponseEntity.ok(books);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load borrowed books"));
        }
    }

    // ==========================
    // ADMIN - DUE SOON LIST
    // ==========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("admin/due-soon")
    public ResponseEntity<?> getDueSoonBooks(@RequestParam(defaultValue = "7") int days) {
        try {
            return ResponseEntity.ok(dashboardService.getDueSoonTransactions(days));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch due-soon books"));
        }
    }

    // ==========================
    // MEMBER RECOMMENDED BOOKS
    // ==========================
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member/{memberId}/recommendedbooks")
    public ResponseEntity<?> getRecommendedBooks(@PathVariable Long memberId) {
        try {
            List<RecommendedBookDTO> books = dashboardService.getRecommendedBooks(memberId);

            if (books == null || books.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No recommendations available"));
            }
            return ResponseEntity.ok(books);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member/{memberId}/mybooks")
    public ResponseEntity<?> getMyBooks(@PathVariable Long memberId){
        try {
            List<MyBooksDTO> books=dashboardService.getMyBooks(memberId);

            if(books == null || books.isEmpty()){
                return ResponseEntity.ok(Map.of("message", "No Borrowed books"));
            }

            return ResponseEntity.ok(books);
        } catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

}
