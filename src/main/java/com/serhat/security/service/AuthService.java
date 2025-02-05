package com.serhat.security.service;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.exception.InvalidCredentialsException;
import com.serhat.security.exception.TokenNotFoundException;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.mapper.AuthMapper;
import com.serhat.security.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService blacklistService;
    private final AuthMapper authMapper;
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.username());

        User user = findUserByUsername(request.username());
        validatePassword(request.password(), user.getPassword());

        String token = jwtUtil.generateToken(user, user.getRole());
        jwtUtil.saveUserToken(user, token);

        log.info("Login successful for user: {}", request.username());
        return authMapper.createAuthResponse(token, user.getUsername(), user.getRole(), "Login Successful!");
    }

    @Transactional
    public AuthResponse logout(HttpServletRequest request) {
        log.info("Processing logout request");

        String jwtToken = jwtUtil.getTokenFromAuthorizationHeader(request);
        jwtUtil.invalidateToken(jwtToken);
        blacklistService.blacklistToken(jwtToken);

        request.getSession().invalidate();

        String username = jwtUtil.extractUsername(jwtToken);
        Role role = jwtUtil.extractRole(jwtToken);
        log.info("Logout successful for user: {}", username);
        log.info("Session Invalidated After logout request from: {}", username);

        return authMapper.createAuthResponse(jwtToken, username, role, "Logout successful");
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
}
