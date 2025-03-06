package com.serhat.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serhat.security.exception.InvalidTokenException;
import com.serhat.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator; // Explicitly use JwtValidator
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register",
            "/v3/api-docs",
            "/swagger-ui/",
            "/swagger-resources/",
            "/actuator/",
            "/health"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String requestId = request.getHeader("X-Request-ID");
            if (requestId == null) {
                requestId = UUID.randomUUID().toString();
            }
            org.slf4j.MDC.put("requestId", requestId);
            response.setHeader("X-Request-ID", requestId);

            log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

            String jwt = jwtValidator.getTokenFromAuthorizationHeader(request);
            if (jwt == null) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    log.debug("Cookies found: {}", Arrays.toString(cookies));
                    for (Cookie cookie : cookies) {
                        log.debug("Cookie name: {}, value: {}", cookie.getName(), cookie.getValue());
                        if ("token".equals(cookie.getName())) {
                            jwt = cookie.getValue();
                            log.debug("JWT found in cookie: {}", jwt);
                            break;
                        }
                    }
                } else {
                    log.debug("No cookies present in request");
                }
            }

            if (jwt == null) {
                log.debug("No JWT found for request: {}", request.getRequestURI());
                if (request.getRequestURI().startsWith("/api/")) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Access denied. No token provided. Please log in.");
                } else {
                    filterChain.doFilter(request, response);
                }
                return;
            }

            authenticateWithToken(request, response, filterChain, jwt);

        } catch (Exception e) {
            log.error("Unexpected error in JWT filter", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while processing your request");
        } finally {
            org.slf4j.MDC.clear();
        }
    }

    private void authenticateWithToken(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            String jwt
    ) throws IOException, ServletException {
        try {
            String username = jwtValidator.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Processing token for username: {}", username);

                if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                    log.warn("Token is blacklisted for user: {}", username);
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Token is blacklisted. Please log in again.");
                    return;
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtValidator.validateToken(jwt, userDetails)) {
                    log.warn("Invalid or expired token for user: {}", username);
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid or expired token. Please log in again.");
                    return;
                }

                setAuthentication(request, userDetails, jwt);
            }

            filterChain.doFilter(request, response);

        } catch (InvalidTokenException e) {
            log.warn("Invalid token: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (AccessDeniedException e) {
            log.warn("Access denied: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. You do not have permission to access this resource.");
        } catch (Exception e) {
            log.error("Error in token authentication", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred. Please try again later.");
        }
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails, String jwt) {
        try {
            String role = jwtValidator.extractRole(jwt);
            log.debug("Valid token found for user: {} with role from token: {}",
                    userDetails.getUsername(), role);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    Collections.singletonList(authority)
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("Authentication set in SecurityContext for user: {} with authorities: {}",
                    userDetails.getUsername(), authToken.getAuthorities());
        } catch (Exception e) {
            log.error("Error setting authentication", e);
            throw e;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status);
        errorDetails.put("error", getErrorTypeForStatus(status));
        errorDetails.put("message", message);
        errorDetails.put("timestamp", System.currentTimeMillis());

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    private String getErrorTypeForStatus(int status) {
        return switch (status) {
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 400 -> "Bad Request";
            default -> "Error";
        };
    }
}