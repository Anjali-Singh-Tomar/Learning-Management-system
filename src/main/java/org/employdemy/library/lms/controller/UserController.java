package org.employdemy.library.lms.controller;

import jakarta.annotation.security.PermitAll;
import org.employdemy.library.lms.dto.ApiResponse;
import org.employdemy.library.lms.dto.UserRequestDTO;
import org.employdemy.library.lms.dto.UserResponseDTO;
import org.employdemy.library.lms.dto.UserUpdateDTO;
import org.employdemy.library.lms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---------------------------------------------------------
    // CREATE USER  (ADMIN ONLY)
    // ---------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO dto) {
        try {
            return ResponseEntity.ok(userService.createUser(dto));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // ---------------------------------------------------------
    // FETCH ALL USERS WITH NUMBER OF BOOKS BORROWED IN LIFETIME
    // ---------------------------------------------------------
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Unable to fetch user list"
            ));
        }
    }

    // ---------------------------------------------------------
    // FETCH USER BY ID
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "User not found with ID: " + id
            ));
        }
    }

    // ---------------------------------------------------------
    // UPDATE USER BY ID
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto
    ) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // ---------------------------------------------------------
    // DELETE USER
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "User not found with ID: " + id
            ));
        }
    }

    @PutMapping("/activate/{userId}")
    public ResponseEntity<ApiResponse<String>> activateUser(@RequestParam Boolean active,@PathVariable Long userId){

        try{

           return ResponseEntity.ok(
                    ApiResponse.success(
                            200,
                            "User Activation/Deactivation is successfull",
                            userService.getActivateUser(active,userId)
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(
                            500,
                            "User Activation/Deactivation is not successfull",
                            e.getMessage()
                    )
            );
        }
    }
}
