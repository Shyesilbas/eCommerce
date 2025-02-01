package com.serhat.security.service;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.entity.Token;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.entity.enums.TokenStatus;
import com.serhat.security.exception.InvalidCredentialsException;
import com.serhat.security.exception.TokenNotFoundException;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.repository.TokenRepository;
import com.serhat.security.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService blacklistService;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        log.info("Attempting login for user: {}", request.username());

        User user = findUserByUsername(request.username());
        validatePassword(request.password(), user.getPassword());

        String token = jwtUtil.generateToken(user, user.getRole());
        saveUserToken(user, token);

        log.info("Login successful for user: {}", request.username());
        return createAuthResponse(token, user.getUsername(), user.getRole(), "Login Successful!");
    }

    @Transactional
    public AuthResponse logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Processing logout request");

        String jwtToken = extractJwtFromAuthorizationHeader(request);
        invalidateToken(jwtToken);
        blacklistService.blacklistToken(jwtToken);

        request.getSession().invalidate();

        String username = jwtUtil.extractUsername(jwtToken);
        Role role = jwtUtil.extractRole(jwtToken);
        log.info("Logout successful for user: {}", username);
        log.info("Session Invalidated After logout request from: {}", username);

        return createAuthResponse(jwtToken, username, role, "Logout successful");
    }

    private String extractJwtFromAuthorizationHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new TokenNotFoundException("JWT not found in Authorization header");
    }


    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.warn("Invalid password attempt");
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(() -> new RuntimeException("No cookies found"));

        return Arrays.stream(cookies)
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new TokenNotFoundException("JWT not found in cookies"));
    }

    private void invalidateToken(String jwtToken) {
        Token token = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        token.setTokenStatus(TokenStatus.LOGGED_OUT);
        token.setExpired_at(Date.from(Instant.now()));
        tokenRepository.save(token);
        log.debug("Token invalidated: {}", jwtToken);
    }

    private void saveUserToken(User user, String token) {
        Token newToken = Token.builder()
                .username(user.getUsername())
                .token(token)
                .createdAt(new Date())
                .expiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .tokenStatus(TokenStatus.ACTIVE)
                .build();

        tokenRepository.save(newToken);
        log.debug("Token saved for user: {}", user.getUsername());
    }

    private AuthResponse createAuthResponse(String token, String username, Role role, String message) {
        return AuthResponse.builder()
                .token(token)
                .username(username)
                .role(role)
                .message(message)
                .build();
    }
}
