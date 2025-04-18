package com.serhat.ecommerce.auth.authService;

import com.serhat.ecommerce.auth.dto.LoginRequest;
import com.serhat.ecommerce.auth.dto.RegisterRequest;
import com.serhat.ecommerce.dto.response.AuthResponse;
import com.serhat.ecommerce.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    String logout(HttpServletRequest request);
}
