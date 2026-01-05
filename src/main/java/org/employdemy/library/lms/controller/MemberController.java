package org.employdemy.library.lms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.service.MemberService;
import org.employdemy.library.lms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final UserService userService;

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
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
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

            return ResponseEntity.ok(books);
        } catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    //-----------------------------------------------------------------------------
    //MEMBER SETTINGS
    //-----------------------------------------------------------------------------

    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN')")
    @PutMapping("/sidebar/{id}/name")
    public ResponseEntity<ApiResponse<Void>> updateName(
            @PathVariable Long id,
            @RequestBody @Valid UpdateNameDTO dto){

        try{
            memberService.updateUserName(id,dto.getName());

            return ResponseEntity.ok(
                    ApiResponse.success(
                            200,
                            "Name updated Successfully",
                            null
                    )
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(
                            400,
                            "Failed to update name",
                            e.getMessage()
                    )
            );
        }
    }

    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN')")
    @PutMapping("/sidebar/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestBody @Valid ChangePasswordDTO  dto){


        try{
            memberService.changePassword(id,dto);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            200,
                            "Password changed successfully",
                            null
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(
                            400,
                            "Password change failed",
                            e.getMessage()
                    )
            );
        }
    }
}
