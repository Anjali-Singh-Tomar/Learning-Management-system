package org.employdemy.library.lms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomJwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String message = "Unauthorized: Missing or invalid token";

        // If JWT filter placed a custom message
        if (request.getAttribute("jwt_error") != null) {
            message = request.getAttribute("jwt_error").toString();
        }

        Map<String, String> error = Map.of("error", message);

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
