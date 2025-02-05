package com.serhat.security.controller;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.request.RegisterRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.dto.response.RegisterResponse;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.service.AuthService;
import com.serhat.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request){
        log.debug("Received Register Request ");
        return ResponseEntity.ok(userService.register(request));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/test/CUSTOMER")
    public String testCustomer(){
        return "Only customer can access";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test/ADMIN")
    public String testAdmin(){
        return "Only admin can access";
    }
}
