package com.serhat.security.service;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.entity.Token;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.TokenStatus;
import com.serhat.security.exception.InvalidTokenFormat;
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

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    @Transactional
    public AuthResponse login(LoginRequest request , HttpServletResponse response) {
        log.info("Attempting login for user: {}", request.username());

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", request.username());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user, user.getRole());
        saveUserToken(user, token);

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);



        log.info("Login successful for user: {}", request.username());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    @Transactional
    public AuthResponse logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Processing logout request");

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("No cookies found");
        }

        String jwtToken = null;
        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                jwtToken = cookie.getValue();
                break;
            }
        }

        if (jwtToken == null) {
            throw new RuntimeException("JWT not found in cookies");
        }

        String username = jwtUtil.extractUsername(jwtToken);

        Token userToken = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        userToken.setTokenStatus(TokenStatus.LOGGED_OUT);
        userToken.setExpired_at(LocalDateTime.now());
        tokenRepository.save(userToken);

        Cookie logoutCookie = new Cookie("jwt", null);
        logoutCookie.setHttpOnly(true);
        logoutCookie.setSecure(true);
        logoutCookie.setPath("/");
        logoutCookie.setMaxAge(0);
        response.addCookie(logoutCookie);

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