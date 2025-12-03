package org.employdemy.library.lms.config;

import org.employdemy.library.lms.security.CustomJwtEntryPoint;
import org.employdemy.library.lms.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomJwtEntryPoint customJwtEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService, CustomJwtEntryPoint customJwtEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.customJwtEntryPoint=customJwtEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // custom error handler for missing token
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(customJwtEntryPoint)
                )

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

                        // Public GET access for members
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()

                        // Admin + Librarian can create/update/delete
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAnyRole("ADMIN", "LIBRARIAN")


                        // TRANSACTIONS MODULE
                        // Admin + Librarian → FULL access
                        // Members → only their own allowed
                        .requestMatchers("/api/transactions/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")

                        //Dashboard Module
                        .requestMatchers("/api/dashboard/admin/**")
                        .hasRole("ADMIN")

                        //Dashboard Module
                        .requestMatchers("/api/dashboard/member/**")
                        .hasRole("MEMBER")

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
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(10));
        return provider;
    }
}
