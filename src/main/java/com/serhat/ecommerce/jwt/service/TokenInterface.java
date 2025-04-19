package com.serhat.ecommerce.jwt.service;

import com.serhat.ecommerce.user.enums.Role;
import jakarta.servlet.http.HttpServletRequest;

public interface TokenInterface {
    String extractTokenFromRequest(HttpServletRequest request);
    void validateRole(HttpServletRequest request, Role... allowedRoles);
}