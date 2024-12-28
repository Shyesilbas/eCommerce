package com.serhat.security.controller;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.debug("Received login request for user: {}", request.username());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestHeader("Authorization") String bearerToken) {
        log.debug("Received logout request");
        return ResponseEntity.ok(authService.logout(bearerToken));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AuthResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(AuthResponse.builder()
                        .message(ex.getMessage())
                        .build());
    }

    @GetMapping("/test/CUSTOMER")
    public String testCustomer(){
        return "Only customer can access";
    }
}
