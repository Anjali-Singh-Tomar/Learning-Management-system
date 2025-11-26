package org.employdemy.library.lms.security;

import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        // 1️⃣ Admin login
        if (identifier.equals("admin@lms.com") || identifier.equals("EMP0000")) {
            return org.springframework.security.core.userdetails.User
                    .withUsername("admin@lms.com")
                    .password("{noop}admin123")
                    .roles("ADMIN")
                    .build();
        }

        // 2️⃣ User login by email
        User user = userRepository.findByEmail(identifier).orElse(null);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        // 3️⃣ User login by empId
        user = userRepository.findByEmpId(identifier).orElse(null);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        throw new UsernameNotFoundException("User not found with: " + identifier);
    }
}
