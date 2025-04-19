package com.serhat.ecommerce.auth.dto.response;

import com.serhat.ecommerce.user.enums.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
         String token,
         String username,
         Role role,
         String message
) {
}
