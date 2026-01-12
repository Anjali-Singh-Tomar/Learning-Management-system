package org.employdemy.library.lms.controller;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.dto.ApiResponse;
import org.employdemy.library.lms.dto.IssueBookRequestDTO;
import org.employdemy.library.lms.service.LibrarianService;
import org.employdemy.library.lms.service.TransactionService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<List<ManageMembersDTO>>> manageMembers(){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "All Members are fetched Successfully",
                        librarianService.manageMembers()
                )
        );
    }

    @GetMapping("/sidebar/pending-request")
    public ResponseEntity<ApiResponse<?>> pendingRequest(){

        List<PendingResponseDTO> data=transactionService.getAllRequests();

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Request data fetched",
                        data
                )
        );
    }

    @GetMapping("/sidebar/borrowedBooks")
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    public ResponseEntity<ApiResponse<?>> borrowedBooks(){

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Borrowed Books fetched Successfully",
                        librarianService.getBorrowedBooks()
                )
        );
    }


    @PostMapping("/sidebar/borrowedBooks/{id}")
    public ResponseEntity<ApiResponse<String>> markReturned(@PathVariable Long id){

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "The Transaction is Marked as Returned Successfully",
                        librarianService.markAsReturned(id)
                )
        );
    }

//    @PreAuthorize(("hasRole('LIBRARIAN')"))
//    @GetMapping("/sidebar/memberDetails/{userId}")
//    public ResponseEntity<ApiResponse<MemberDetailsDTO>> memberDetails(@PathVariable Long userId){
//
//        try{
//            return ResponseEntity.ok(
//                    ApiResponse.success(
//                            200,
//                            "Member Details fetched Successfully",
//                            librarianService.getmemberDetails(userId)
//                    )
//            );
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(
//                    ApiResponse.error(
//                            500,
//                            "Fetching of Member Details Failed",
//                            e.getMessage()
//                    )
//            );
//        }
//    }

    @GetMapping("/searchMembers")
    public ResponseEntity<ApiResponse<List<ManageMembersDTO>>> searchMembers(@RequestParam String keyword){

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Member Found",
                        librarianService.getSearchMembers(keyword)
                )
        );
    }

    @GetMapping("/filterMembers")
    public ResponseEntity<ApiResponse<List<ManageMembersDTO>>> filterMembers(@RequestParam Boolean status){

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Filter By status is fetched successfully",
                        librarianService.getFilterMembers(status)
                )
        );
    }

}
