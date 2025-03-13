package com.serhat.security.jwt;

import com.serhat.security.entity.Token;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.TokenStatus;
import com.serhat.security.repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtProvider implements JwtOperations {

    @Value("${security.jwt.expiration-time}")
    private long expiration;

    @Value("${security.jwt.secret-key}")
    private String secret;
    private final TokenRepository tokenRepository;

    public JwtProvider(TokenRepository tokenRepository) {
        this.tokenRepository=tokenRepository;
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");
        claims.put("role", role);

        Long userId;
        if (userDetails instanceof User) {
            userId = ((User) userDetails).getUserId();
        } else {
            throw new IllegalArgumentException("UserDetails does not contain userId");
        }
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public void saveToken(UserDetails userDetails, String token) {
        try {
            Token newToken = Token.builder()
                    .username(userDetails.getUsername())
                    .token(token)
                    .createdAt(new Date())
                    .expiresAt(new Date(System.currentTimeMillis() + expiration))
                    .tokenStatus(TokenStatus.ACTIVE)
                    .build();

            tokenRepository.save(newToken);
        } catch (Exception e) {
            log.error("Error saving token", e);
            throw new RuntimeException("Error saving token", e);
        }
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }
    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Placeholder implementations (these are handled by JwtValidator)
    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        throw new UnsupportedOperationException("Token validation is handled by JwtValidator");
    }

    @Override
    public String getTokenFromAuthorizationHeader(HttpServletRequest request) {
        throw new UnsupportedOperationException("Token extraction from header is handled by JwtValidator");
    }

    @Override
    public void invalidateToken(String token) {
        throw new UnsupportedOperationException("Token invalidation is handled by JwtValidator");
    }
}