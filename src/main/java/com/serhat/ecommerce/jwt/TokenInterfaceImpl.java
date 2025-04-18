package com.serhat.ecommerce.jwt;

import com.serhat.ecommerce.enums.Role;
import com.serhat.ecommerce.user.userS.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TokenInterfaceImpl implements TokenInterface {

    protected final JwtOperations jwtOperations;
    protected final UserRepository userRepository;

    public TokenInterfaceImpl(@Qualifier("jwtValidator") JwtOperations jwtOperations, UserRepository userRepository) {
        this.jwtOperations = jwtOperations;
        this.userRepository = userRepository;
    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> "jwt".equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElse(null);
    }

    @Override
    public void validateRole(HttpServletRequest request, Role... allowedRoles) {
        String token = extractTokenFromRequest(request);
        Role userRole = Role.valueOf(jwtOperations.extractRole(token));
        if (Stream.of(allowedRoles).noneMatch(role -> role == userRole)) {
            throw new RuntimeException("Unauthorized action. Required role: " + List.of(allowedRoles));
        }
    }
}