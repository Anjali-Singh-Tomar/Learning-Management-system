package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.LibrarianOverviewResponse;
import org.employdemy.library.lms.service.LibrarianService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/librarian")
@RequiredArgsConstructor
@CrossOrigin
public class LibrarianController {

    private final LibrarianService librarianService;

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

}
