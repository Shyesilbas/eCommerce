package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
public record UserResponse(
        Long userId,
        String email,
        String username,
        String password,
        Role role
) {
}
