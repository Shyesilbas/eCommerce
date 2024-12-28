package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.Role;

public record UserResponse(
        Long userId,
        String email,
        String username,
        String password,
        Role role
) {
}
