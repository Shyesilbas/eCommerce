package com.serhat.security.controller;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.request.RegisterRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.dto.response.RegisterResponse;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.service.AuthService;
import com.serhat.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

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

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        log.debug("Received Register Request ");
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/test/CUSTOMER")
    public String testCustomer(){
        return "Only customer can access";
    }
}
