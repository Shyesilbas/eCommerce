package com.serhat.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtOperations {
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
    Date extractExpiration(String token);
    boolean isTokenExpired(String token);
    boolean validateToken(String token, UserDetails userDetails);
    String getTokenFromAuthorizationHeader(HttpServletRequest request);
    void saveToken( UserDetails userDetails,String token);

    void invalidateToken(String token);
    String extractRole(String token);
    Long extractUserId(String token);
}
