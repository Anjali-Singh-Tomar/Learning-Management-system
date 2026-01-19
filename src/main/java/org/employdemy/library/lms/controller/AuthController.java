package org.employdemy.library.lms.controller;

import org.employdemy.library.lms.dto.ApiResponse;
import org.employdemy.library.lms.dto.AuthRequestDTO;
import org.employdemy.library.lms.dto.AuthResponseDTO;
import org.employdemy.library.lms.model.Role;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.UserRepository;
import org.employdemy.library.lms.security.JwtUtil;
import org.employdemy.library.lms.service.EmailService;
import org.employdemy.library.lms.service.OtpService;
import org.employdemy.library.lms.service.OtpStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final OtpService otpService;
    private final OtpStore otpStore;
    private final EmailService emailService;

    // ADMIN CREDENTIALS
    private static final String ADMIN_EMAIL = "anjali206542@gmail.com";
    private static final String ADMIN_EMPID = "EMP0001";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final Long ADMIN_USER_ID = 0L;

    private static final String libraryName="Employdemy Library";

    public AuthController(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            OtpStore otpStore,
            EmailService emailService
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.otpStore = otpStore;
        this.emailService = emailService;
    }

    // -----------------------------------------------------
    // STEP 1: VERIFY PASSWORD → SEND OTP
    // -----------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {

        String identifier = request.getIdentifier();
        String password = request.getPassword();

        // ------------------------------------
        // 1️⃣ ADMIN LOGIN
        // ------------------------------------
        if ((identifier.equalsIgnoreCase(ADMIN_EMAIL) || identifier.equalsIgnoreCase(ADMIN_EMPID))) {

            if (!password.equals(ADMIN_PASSWORD)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid admin credentials"));
            }

            String otp = otpService.generateOtp();
            otpStore.saveOtp(identifier, otp);
            emailService.sendOtp(ADMIN_EMAIL, otp);

            return ResponseEntity.ok(
                    Map.of("message", "OTP sent to admin email", "identifier", identifier)
            );
        }

        // ------------------------------------
        // 2️⃣ NORMAL USER LOGIN
        // ------------------------------------
        User user = userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByEmpId(identifier).orElse(null));

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid password"));
        }

        if (!user.isActive()) {
            return ResponseEntity.status(403).body(Map.of("error", "User account is inactive"));
        }
 
        // generate OTP for normal user
        String otp = otpService.generateOtp();
        otpStore.saveOtp(identifier, otp);
        emailService.sendOtp(user.getEmail(), otp);

        return ResponseEntity.ok(
                Map.of("message", "OTP sent to your email", "identifier", identifier)
        );
    }


    // -----------------------------------------------------
    // STEP 2: VERIFY OTP → RETURN JWT TOKEN
    // -----------------------------------------------------
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {

        String identifier = request.get("identifier");
        String otp = request.get("otp");

        boolean valid = otpStore.verifyOtp(identifier, otp);

        if (!valid)
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired OTP"));

        // ======================================
        // Identify user/admin and create token
        // ======================================
        Role role;
        Long userId;
        String email;
        String name;

        // Admin
        if (identifier.equalsIgnoreCase(ADMIN_EMAIL) || identifier.equalsIgnoreCase(ADMIN_EMPID)) {
            role = Role.ADMIN;
            userId = ADMIN_USER_ID;
            email = ADMIN_EMAIL;
            name = "System Admin";
        } else {

            // Normal user
            User user = userRepository.findByEmail(identifier)
                    .orElseGet(() -> userRepository.findByEmpId(identifier).orElse(null));

            role = user.getRole();
            userId = user.getId();
            email = user.getEmail();
            name = user.getName();
        }

        // CREATE JWT TOKEN
        String token = jwtUtil.generateToken(identifier, role);

        // Prepare final response
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUserId(userId);
        response.setEmail(email);
        response.setName(name);
        response.setRole(role);

        return ResponseEntity.ok(response);
    }

    //TODO: Modification needed for temporary token
//    @PostMapping("/change-pwd/verify-otp")
//    public ResponseEntity<ApiResponse<?>> verifyOtpForChangePwd(@RequestBody Map<String, String> request) {
//
//        String identifier = request.get("identifier");
//        String otp = request.get("otp");
//
//        Role role;
//        boolean valid = otpStore.verifyOtp(identifier, otp);
//        if (identifier.equalsIgnoreCase(ADMIN_EMAIL) || identifier.equalsIgnoreCase(ADMIN_EMPID)) {
//            role = Role.ADMIN;
//        } else {
//
//            // Normal user
//            User user = userRepository.findByEmail(identifier)
//                    .orElseGet(() -> userRepository.findByEmpId(identifier).orElse(null));
//            role = user.getRole();
//        }
//
//        // CREATE JWT TOKEN
//        String token = jwtUtil.generateToken(identifier, role);
//
//        if (!valid)
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    ApiResponse.error(
//                            HttpStatus.UNAUTHORIZED.value(),
//                            "Invalid or Expired OTP",
//                            token
//                    )
//            );
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                ApiResponse.success(
//                        HttpStatus.OK.value(),
//                        "Verified Succesfully",
//                        token
//                )
//        );
//    }


    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request){
        String identifier=request.get("identifier");

        User user = userRepository.findByEmail(identifier).orElseGet(() -> userRepository.findByEmpId(identifier).orElse(null));

        if ((identifier.equalsIgnoreCase(ADMIN_EMAIL) || identifier.equalsIgnoreCase(ADMIN_EMPID))){
            String otp = otpService.generateOtp();
            otpStore.saveOtp(identifier, otp);
            emailService.sendOtp(ADMIN_EMAIL, otp);

            return ResponseEntity.ok(
                    Map.of("message", "OTP sent again to admin email", "identifier", identifier)
            );
        } else if(user.getEmail().equals(identifier) || user.getEmpId().equals(identifier)){

            String otp = otpService.generateOtp();
            otpStore.saveOtp(identifier, otp);
            emailService.sendOtp(user.getEmail(), otp);
            return ResponseEntity.ok(
                    Map.of("message", "OTP sent again to your email", "identifier", identifier));

        }else {
            return ResponseEntity.ok(Map.of("message", "Entered email/empno is not correct"));
        }


    }
}
