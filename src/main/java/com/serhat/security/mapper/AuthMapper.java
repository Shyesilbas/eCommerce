package com.serhat.security.mapper;

import com.serhat.security.dto.response.AuthResponse;
import com.serhat.security.entity.enums.Role;
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
