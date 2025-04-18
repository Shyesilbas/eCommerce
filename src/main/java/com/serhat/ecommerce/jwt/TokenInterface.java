package com.serhat.ecommerce.jwt;

import com.serhat.ecommerce.enums.Role;
import jakarta.servlet.http.HttpServletRequest;

public interface TokenInterface {
    String extractTokenFromRequest(HttpServletRequest request);
    void validateRole(HttpServletRequest request, Role... allowedRoles);
}