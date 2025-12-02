package org.employdemy.library.lms.controller;

import org.employdemy.library.lms.dto.AuthRequestDTO;
import org.employdemy.library.lms.dto.AuthResponseDTO;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.model.Role;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.UserRepository;
import org.employdemy.library.lms.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    // Unified Admin Credentials
    private static final String ADMIN_EMAIL = "admin@lms.com";
    private static final String ADMIN_EMPID = "EMP0000";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final Long ADMIN_USER_ID=0L;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {

        String identifier = request.getIdentifier();
        String password = request.getPassword();

        // 1️⃣ ADMIN LOGIN (email OR empId)
        if ((identifier.equalsIgnoreCase(ADMIN_EMAIL) || identifier.equalsIgnoreCase(ADMIN_EMPID))
                && password.equals(ADMIN_PASSWORD)) {

            String token = jwtUtil.generateToken(identifier, Role.ADMIN);

            AuthResponseDTO response = new AuthResponseDTO();
            response.setToken(token);
            response.setUserId(ADMIN_USER_ID);
            response.setName("System Admin");
            response.setEmail(ADMIN_EMAIL);
            response.setRole(Role.ADMIN);

            return ResponseEntity.ok(response);
        }
        if (identifier.equalsIgnoreCase(ADMIN_EMAIL) || identifier.equalsIgnoreCase(ADMIN_EMPID)) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Invalid admin credentials"));
        }

        // 2️⃣ NORMAL USER LOGIN (email OR empId)
        User user=null;

        // If user not found
        if (user == null) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Invalid username or password"));
        }

        // If password incorrect
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Invalid username or password"));
        }

        // If inactive user
        if (!user.isActive()) {
            return ResponseEntity
                    .status(403)
                    .body(Map.of("error", "User account is inactive"));
        }


        // Token stores identifier (email OR empId)
        String token = jwtUtil.generateToken(identifier, user.getRole());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);
    }
}
