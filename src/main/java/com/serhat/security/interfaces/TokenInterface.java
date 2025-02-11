package com.serhat.security.interfaces;

import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.Role;
import jakarta.servlet.http.HttpServletRequest;

public interface TokenInterface {
    String extractTokenFromRequest(HttpServletRequest request);
    User getUserFromToken(HttpServletRequest request);

    void validateRole(HttpServletRequest request, Role... allowedRoles);
}