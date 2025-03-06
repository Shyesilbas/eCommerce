package com.serhat.security.service.auth;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.request.RegisterRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.dto.response.RegisterResponse;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.service.user.UserValidationService;
import com.serhat.security.jwt.JwtOperations;
import com.serhat.security.component.mapper.AuthMapper;
import com.serhat.security.component.mapper.UserMapper;
import com.serhat.security.repository.UserRepository;
import com.serhat.security.jwt.TokenBlacklistService;
import com.serhat.security.service.UserDetailsServiceImpl;
import com.serhat.security.service.auth.password.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtOperations jwtOperations;
    private final PasswordService passwordService;
    private final TokenBlacklistService blacklistService;
    private final AuthMapper authMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    public AuthServiceImpl(@Qualifier("jwtValidator") JwtOperations jwtOperations,
                           PasswordService passwordService,
                           TokenBlacklistService blacklistService,
                           AuthMapper authMapper, UserDetailsServiceImpl userDetailsService,
                           UserMapper userMapper, UserRepository userRepository, UserValidationService userValidationService) {
        this.jwtOperations = jwtOperations;
        this.passwordService = passwordService;
        this.blacklistService = blacklistService;
        this.authMapper = authMapper;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.userValidationService = userValidationService;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.username());

        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        passwordService.validatePassword(request.password(), user.getPassword());

        String accessToken = jwtOperations.generateToken(user);
        jwtOperations.saveToken(user, accessToken);

        log.info("Login successful for user: {}", request.username());
        return authMapper.createAuthResponse(accessToken,
                user.getUsername(),
                Role.valueOf(user.getAuthorities().iterator().next().getAuthority()),
                "Login Successful!");
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        userValidationService.validateUserRegistration(request);

        User user = userMapper.toUser(request);
        userRepository.save(user);

        return new RegisterResponse(
                "Register Successful! Now you can login with your credentials.",
                user.getUsername(),
                user.getEmail(),
                user.getMembershipPlan(),
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional
    public String logout(HttpServletRequest request) {
        log.info("Processing logout request");

        String jwtToken = jwtOperations.getTokenFromAuthorizationHeader(request);
        jwtOperations.invalidateToken(jwtToken);
        blacklistService.blacklistToken(jwtToken);

        String username = jwtOperations.extractUsername(jwtToken);
        log.info("Logout successful for user: {}", username);

        return "Logout successful";
    }
}
