package org.employdemy.library.lms.config;

import org.employdemy.library.lms.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Allow PUT only for roles (important)
                        .requestMatchers(HttpMethod.PUT, "/api/books/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        // ===============================
                        // ROLE-BASED ACCESS CONTROL
                        // ===============================

                        // ADMIN ONLY — user management
                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                        // BOOK MODULE (Admin + Librarian)
                        .requestMatchers("/api/books/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        // TRANSACTIONS MODULE
                        // Admin + Librarian → FULL access
                        // Members → only their own allowed
                        .requestMatchers("/api/transactions/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")

                        //Dashboard Module
                        .requestMatchers("/api/dashboard/admin")
                        .hasRole("ADMIN")

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                );

        // H2 console frame support
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        // Add JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
