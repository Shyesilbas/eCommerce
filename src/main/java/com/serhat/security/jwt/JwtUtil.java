package com.serhat.security.jwt;

import com.serhat.security.entity.Token;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.entity.enums.TokenStatus;
import com.serhat.security.exception.InvalidTokenException;
import com.serhat.security.exception.TokenNotFoundException;
import com.serhat.security.repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.expiration-time}")
    private long expiration;

    private final TokenRepository tokenRepository;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user, Role role) {
        log.info("Generating token for user: {}", user.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Token generated successfully for user: {}", user.getUsername());
        return token;
    }

    public String extractUsername(String token) {
        log.debug("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    public Role extractRole(String token) {
        log.debug("Extracting role from token");

        String roleName = extractClaim(token, claims -> claims.get("role", String.class));
        return Role.valueOf(roleName);
    }

    public Date extractExpiration(String token) {
        log.debug("Extracting expiration from token");
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenInvalid(String token) {
        try {
            final Date expiration = extractExpiration(token);
            Token storedToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Token not found in database"));

            boolean isExpiredByDate = expiration.before(new Date());
            boolean isExpiredByStatus = storedToken.getTokenStatus() != TokenStatus.ACTIVE;

            boolean isInvalid = isExpiredByDate || isExpiredByStatus;

            log.info("Token invalidity check - Is Invalid: {}", isInvalid);
            log.info("Invalidity details - By Date: {}, By Status: {}", isExpiredByDate, isExpiredByStatus);

            return isInvalid;
        } catch (Exception e) {
            log.error("Error checking token validity", e);
            throw new InvalidTokenException("Error checking token validity");
        }
    }

    public boolean validateToken(String token, User user) {
        log.info("Validating token for user: {}", user.getUsername());
        try {
            final String username = extractUsername(token);
            Token storedToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Token not found in database"));

            boolean isValid = username.equals(user.getUsername()) && !isTokenInvalid(token);

            log.info("Token validation result: {}", isValid);

            return isValid;
        } catch (Exception e) {
            log.error("Error validating token", e);
            throw new InvalidTokenException("Token validation failed");
        }
    }

    public String getTokenFromAuthorizationHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("JWT token not found in Authorization header");
    }

    public void invalidateToken(String jwtToken) {
        Token token = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        token.setTokenStatus(TokenStatus.LOGGED_OUT);
        token.setExpired_at(new Date());
        tokenRepository.save(token);
        log.debug("Token invalidated: {}", jwtToken);
    }

    public void saveUserToken(User user, String token) {
        Token newToken = Token.builder()
                .username(user.getUsername())
                .token(token)
                .createdAt(new Date())
                .expiresAt(new Date(System.currentTimeMillis() + expiration))
                .tokenStatus(TokenStatus.ACTIVE)
                .build();

        tokenRepository.save(newToken);
        log.debug("Token saved for user: {}", user.getUsername());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extracting claim from token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.debug("Extracting all claims from token");
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
