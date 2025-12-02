package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.AdminOverviewResponse;
import org.employdemy.library.lms.dto.DueSoonResponseDTO;
import org.employdemy.library.lms.dto.OverdueRecordDTO;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/overdue")
    public ResponseEntity<List<OverdueRecordDTO>> getOverdueBooks() {
        return ResponseEntity.ok(dashboardService.getOverdueBooks());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("admin/due-soon")
    public ResponseEntity<List<DueSoonResponseDTO>> getDueSoonBooks(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(dashboardService.getDueSoonTransactions(days));
    }


}
