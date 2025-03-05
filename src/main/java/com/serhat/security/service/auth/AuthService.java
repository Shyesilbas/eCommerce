package com.serhat.security.service.auth;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.exception.InvalidCredentialsException;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.mapper.AuthMapper;
import com.serhat.security.repository.UserRepository;
import com.serhat.security.service.auth.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService blacklistService;
    private final AuthMapper authMapper;
    private final UserDetailsServiceImpl userDetailsService;
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.username());

        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        validatePassword(request.password(), user.getPassword());

        String accessToken = jwtUtil.generateToken(user);
        jwtUtil.saveUserToken(user,accessToken);

        log.info("Login successful for user: {}", request.username());
        return authMapper.createAuthResponse(accessToken,
                user.getUsername(),
                Role.valueOf(user.getAuthorities().iterator().next().getAuthority()),
                "Login Successful!");
    }

    @Transactional
    public AuthResponse logout(HttpServletRequest request) {
        log.info("Processing logout request");

        String jwtToken = jwtUtil.getTokenFromAuthorizationHeader(request);
        jwtUtil.invalidateToken(jwtToken);
        blacklistService.blacklistToken(jwtToken);


        String username = jwtUtil.extractUsername(jwtToken);
        Role role = jwtUtil.extractRole(jwtToken);
        log.info("Logout successful for user: {}", username);
        log.info("Session Invalidated After logout request from: {}", username);

        return authMapper.createAuthResponse(jwtToken, username, role, "Logout successful");
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.warn("Invalid password attempt");
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }
}
