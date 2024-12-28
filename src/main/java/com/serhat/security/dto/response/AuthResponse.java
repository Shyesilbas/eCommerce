package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
         String token,
         String username,
         Role role,
         String message
) {
}
