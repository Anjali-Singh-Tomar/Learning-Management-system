package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard/overview")
    public ResponseEntity<?> getAdminOverview() {
        try {
            return ResponseEntity.ok(adminService.getAdminOverview());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load admin overview"));
        }
    }



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard/overdue")
    public ResponseEntity<?> getOverdueBooks() {
        try {
            return ResponseEntity.ok(adminService.getOverdueBooks());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch overdue books"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("dashboard/due-soon")
    public ResponseEntity<?> getDueSoonBooks(@RequestParam(defaultValue = "7") int days) {
        try {
            return ResponseEntity.ok(adminService.getDueSoonTransactions(days));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sidebar/manageUsers")
    public ResponseEntity<ApiResponse<List<AdminManageUsersDTO>>> manageUsers(){

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "All the users are fetched successfully",
                        adminService.getManageUsers()
                )
        );
    }

    @GetMapping("/searchAllMembers")
    public ResponseEntity<ApiResponse<List<ManageMembersDTO>>> searchAllMembers(@RequestParam String keyword){

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Member Found",
                        adminService.getSearchMembers(keyword)
                )
        );
    }

    @GetMapping("/borrower-activity-graph")
    public ResponseEntity<ApiResponse<List<BorrowerActivityDTO>>> borrowerActivity(){

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Graph Data Retrieved",
                        adminService.getBorrowerGraph()
                )
        );
    }
}
