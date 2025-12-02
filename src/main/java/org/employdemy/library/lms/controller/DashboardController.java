package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/overview")
    public ResponseEntity<AdminOverviewResponse> getAdminOverview() {
        return ResponseEntity.ok(dashboardService.getAdminOverview());
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member/{memberId}/overview")
    public ResponseEntity<MemberDashboardResponseDTO> getMemberOverview(@PathVariable Long memberId){
        return ResponseEntity.ok(dashboardService.getMemberOverview(memberId));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member/{memberId}/borrowedbooks")
    public ResponseEntity<List<BorrowedBookDTO>> getBorrowedBooks(@PathVariable Long memberId){
        return ResponseEntity.ok(dashboardService.getBorrowedBooks(memberId));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member/{memberId}/recommendedbooks")
    public ResponseEntity<List<RecommendedBookDTO>> getRecommendedBooks(@PathVariable Long memberId){
        return ResponseEntity.ok(dashboardService.getRecommendedBooks(memberId));
    }
}
