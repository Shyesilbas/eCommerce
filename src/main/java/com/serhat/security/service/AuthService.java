package com.serhat.security.service;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.entity.Token;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.TokenStatus;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.repository.TokenRepository;
import com.serhat.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.username());

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!request.password().equals(user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", request.username());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken((UserDetails) user, user.getRole());
        saveUserToken(user, token);

        log.info("Login successful for user: {}", request.username());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    @Transactional
    public AuthResponse logout(String token) {
        log.info("Processing logout request");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token format");
        }

        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);

        Token userToken = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        userToken.setTokenStatus(TokenStatus.EXPIRED);
        tokenRepository.save(userToken);

        log.info("Logout successful for user: {}", username);
        return AuthResponse.builder()
                .message("Logout successful")
                .username(username)
                .role(jwtUtil.extractRole(jwtToken))
                .build();
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
}