package org.employdemy.library.lms.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.employdemy.library.lms.model.Role;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // SECRET should be 32+ chars
    private static final String SECRET = "supersecretjwtkeysupersecretjwtkey123";
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 10; // 10 hours

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * subject = email OR empId
     */
    public String generateToken(String identifier, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(identifier) // ⭐ email or empId stored here
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract identifier (email or empId)
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();  // ⭐ same method name, new meaning
    }

    public String extractRole(String token) {
        Object role = parseClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT Expired");
        } catch (UnsupportedJwtException ex) {
            System.out.println("JWT Unsupported");
        } catch (MalformedJwtException ex) {
            System.out.println("JWT Malformed");
        } catch (SignatureException ex) {
            System.out.println("JWT Signature invalid");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT Illegal argument");
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
