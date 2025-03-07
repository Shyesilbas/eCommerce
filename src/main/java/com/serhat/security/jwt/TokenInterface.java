package com.serhat.security.jwt;

import com.serhat.security.entity.enums.Role;
import jakarta.servlet.http.HttpServletRequest;

public interface TokenInterface {
    String extractTokenFromRequest(HttpServletRequest request);
    void validateRole(HttpServletRequest request, Role... allowedRoles);
}