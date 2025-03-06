package com.serhat.security.service.auth;

import com.serhat.security.dto.request.LoginRequest;
import com.serhat.security.dto.request.RegisterRequest;
import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    String logout(HttpServletRequest request);
}
