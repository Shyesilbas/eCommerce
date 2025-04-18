package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
         String token,
         String username,
         Role role,
         String message
) {
}
