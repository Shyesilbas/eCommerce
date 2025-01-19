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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request , HttpServletResponse response) {
        log.debug("Received login request for user: {}", request.username());
        return ResponseEntity.ok(authService.login(request,response));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            authService.logout(request, response);
            return ResponseEntity.ok("Logged out successfully.");
        } catch (Exception e) {
            log.error("Logout error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed: " + e.getMessage());
        }
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
}
