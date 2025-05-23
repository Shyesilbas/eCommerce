package com.serhat.ecommerce.jwt.service;

import com.serhat.ecommerce.jwt.enums.TokenStatus;
import com.serhat.ecommerce.jwt.TokenException.InvalidTokenException;
import com.serhat.ecommerce.jwt.repo.TokenRepository;
import com.serhat.ecommerce.jwt.entity.Token;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
public class JwtValidator implements JwtOperations {

    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    public JwtValidator(JwtProvider jwtProvider, TokenRepository tokenRepository) {
        this.jwtProvider = jwtProvider;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Long extractUserId(String token) {
        return jwtProvider.extractUserId(token);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = jwtProvider.extractUsername(token);
            tokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Token not found in database"));

            return username.equals(userDetails.getUsername()) && !isTokenInvalid(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token has expired");
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new InvalidTokenException("Invalid JWT token");
        } catch (Exception e) {
            throw new InvalidTokenException("Token validation failed: " + e.getMessage());
        }
    }

    private boolean isTokenInvalid(String token) {
        try {
            boolean isExpiredByDate = jwtProvider.isTokenExpired(token);
            Token storedToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Token not found in database"));
            boolean isExpiredByStatus = storedToken.getTokenStatus() != TokenStatus.ACTIVE;
            return isExpiredByDate || isExpiredByStatus;
        } catch (Exception e) {
            log.error("Error checking token validity", e);
            throw new InvalidTokenException("Error checking token validity");
        }
    }

    @Override
    public String getTokenFromAuthorizationHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public void saveToken(UserDetails userDetails, String token) {
        jwtProvider.saveToken(userDetails, token);
    }

    @Override
    public void invalidateToken(String token) {
        Token storedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token not found in database"));
        storedToken.setTokenStatus(TokenStatus.LOGGED_OUT);
        storedToken.setExpired_at(Date.from(Instant.now()));
        tokenRepository.save(storedToken);
    }


    // Delegate to JwtProvider for these methods
    @Override
    public String generateToken(UserDetails userDetails) {
        return jwtProvider.generateToken(userDetails);
    }

    @Override
    public String extractUsername(String token) {
        return jwtProvider.extractUsername(token);
    }

    @Override
    public Date extractExpiration(String token) {
        return jwtProvider.extractExpiration(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return jwtProvider.isTokenExpired(token);
    }

    @Override
    public String extractRole(String token) {
        return jwtProvider.extractRole(token);
    }
}