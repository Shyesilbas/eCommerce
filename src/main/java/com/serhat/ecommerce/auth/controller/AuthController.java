package com.serhat.ecommerce.auth.controller;

import com.serhat.ecommerce.auth.authService.AuthService;
import com.serhat.ecommerce.auth.dto.request.LoginRequest;
import com.serhat.ecommerce.auth.dto.request.RegisterRequest;
import com.serhat.ecommerce.auth.dto.response.AuthResponse;
import com.serhat.ecommerce.auth.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.debug("Received login request for user: {}", request.username());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request){
        log.debug("Received Register Request ");
        return ResponseEntity.ok(authService.register(request));
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
