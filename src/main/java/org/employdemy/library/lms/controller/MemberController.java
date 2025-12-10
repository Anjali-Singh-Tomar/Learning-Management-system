package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.BorrowedBookDTO;
import org.employdemy.library.lms.dto.MemberDashboardResponseDTO;
import org.employdemy.library.lms.dto.MyBooksDTO;
import org.employdemy.library.lms.dto.RecommendedBookDTO;
import org.employdemy.library.lms.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/dashboard/{memberId}/overview")
    public ResponseEntity<?> getMemberOverview(@PathVariable Long memberId) {
        try {
            MemberDashboardResponseDTO dto = memberService.getMemberOverview(memberId);

            if (dto == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Member not found"));
            }
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch member overview"));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/dashboard/{memberId}/recommendedbooks")
    public ResponseEntity<?> getRecommendedBooks(@PathVariable Long memberId) {
        try {
            List<RecommendedBookDTO> books = memberService.getRecommendedBooks(memberId);

            if (books == null || books.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No recommendations available"));
            }
            return ResponseEntity.ok(books);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/sidebar/{memberId}/history")
    public ResponseEntity<?> getBorrowedBooks(@PathVariable Long memberId) {
        try {
            List<BorrowedBookDTO> books = memberService.getAllBooks(memberId);

            if (books == null || books.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No borrowed books found"));
            }
            return ResponseEntity.ok(books);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load borrowed books"));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/{memberId}/mybooks")
    public ResponseEntity<?> getMyBooks(@PathVariable Long memberId){
        try {
            List<MyBooksDTO> books=memberService.getMyBooks(memberId);

            if(books == null || books.isEmpty()){
                return ResponseEntity.ok(Map.of("message", "No Borrowed books"));
            }

            return ResponseEntity.ok(books);
        } catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
