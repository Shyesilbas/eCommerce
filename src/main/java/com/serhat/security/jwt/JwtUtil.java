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


    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");
        claims.put("role", role);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Token generated successfully for user: {}", userDetails.getUsername());
        return token;
    }

    public String extractUsername(String token) {
        try {
            log.debug("Extracting username from token");
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("Error extracting username from token", e);
            throw new InvalidTokenException("Unable to extract username from token");
        }
    }

    public Role extractRole(String token) {
        try {
            log.debug("Extracting role from token");
            String roleName = extractClaim(token, claims -> claims.get("role", String.class));
            return Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role in token", e);
            throw new InvalidTokenException("Invalid role in token");
        } catch (Exception e) {
            log.error("Error extracting role from token", e);
            throw new InvalidTokenException("Unable to extract role from token");
        }
    }

    public Date extractExpiration(String token) {
        log.debug("Extracting expiration from token");
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = extractExpiration(token);
            boolean isExpired = expiration.before(new Date());
            log.debug("Token expiration check - Is Expired: {}", isExpired);
            return isExpired;
        } catch (ExpiredJwtException e) {
            log.debug("Token is already expired");
            return true;
        } catch (Exception e) {
            log.error("Error checking token expiration", e);
            throw new InvalidTokenException("Error checking token expiration");
        }
    }
    public boolean isTokenInvalid(String token) {
        try {
            boolean isExpiredByDate = isTokenExpired(token);

            Token storedToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Token not found in database"));

            boolean isExpiredByStatus = storedToken.getTokenStatus() != TokenStatus.ACTIVE;
            boolean isInvalid = isExpiredByDate || isExpiredByStatus;

            log.debug("Token invalidity check - Is Invalid: {}", isInvalid);
            log.debug("Invalidity details - By Date: {}, By Status: {}", isExpiredByDate, isExpiredByStatus);

            return isInvalid;
        } catch (Exception e) {
            log.error("Error checking token validity", e);
            throw new InvalidTokenException("Error checking token validity");
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        log.info("Validating token for user: {}", userDetails.getUsername());
        try {
            final String username = extractUsername(token);

            // Verify token exists in database
            tokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Token not found in database"));

            // Check username matches and token is valid
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenInvalid(token);
            log.debug("Token validation result: {}", isValid);
            return isValid;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired for user: {}", userDetails.getUsername());
            throw new InvalidTokenException("Token has expired");
        } catch (SignatureException e) {
            log.error("Invalid JWT signature");
            throw new InvalidTokenException("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token");
            throw new InvalidTokenException("Invalid JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
            throw new InvalidTokenException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty");
            throw new InvalidTokenException("JWT claims string is empty");
        } catch (Exception e) {
            log.error("Error validating token", e);
            throw new InvalidTokenException("Token validation failed: " + e.getMessage());
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
        try {
            Token token = tokenRepository.findByToken(jwtToken)
                    .orElseThrow(() -> new TokenNotFoundException("Token not found"));

            token.setTokenStatus(TokenStatus.LOGGED_OUT);
            token.setExpired_at(new Date());
            tokenRepository.save(token);
            log.info("Token invalidated: {}", jwtToken.substring(0, 10) + "...");
        } catch (Exception e) {
            log.error("Error invalidating token", e);
            throw new InvalidTokenException("Error invalidating token: " + e.getMessage());
        }
    }

    public void saveUserToken(UserDetails user, String token) {
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
