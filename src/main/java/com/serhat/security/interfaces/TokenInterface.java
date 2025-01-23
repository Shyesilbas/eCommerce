package com.serhat.security.interfaces;

import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface TokenInterface {
    String extractTokenFromRequest(HttpServletRequest request);
    User getUserFromToken(HttpServletRequest request);
}