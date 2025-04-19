package com.serhat.ecommerce.auth.mapper;

import com.serhat.ecommerce.auth.dto.response.AuthResponse;
import com.serhat.ecommerce.user.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse createAuthResponse(String token, String username, Role role, String message) {
        return AuthResponse.builder()
                .token(token)
                .username(username)
                .role(role)
                .message(message)
                .build();
    }
}
