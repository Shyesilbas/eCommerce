package com.serhat.security.jwt;

import com.serhat.security.entity.User;
import com.serhat.security.exception.InvalidTokenException;
import com.serhat.security.service.auth.TokenBlacklistService;
import com.serhat.security.service.auth.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        String jwt = null;
        String username = null;

        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (jwt != null && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                log.info("Processing token for username: {}", username);

                if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                    log.warn("Token is blacklisted for user: {}", username);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token is blacklisted. Please log in again.\"}");
                    return;
                }

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                User user = (User) userDetails;

                if (!jwtUtil.validateToken(jwt, user)) {
                    log.warn("Invalid or expired token for user: {}", username);
                    throw new InvalidTokenException("Invalid or expired token. Please log in again.");
                }

                String role = jwtUtil.extractRole(jwt).name();
                log.info("Valid token found for user: {} with role: {}", username, role);

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        Collections.singletonList(authority)
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Authentication set in SecurityContext for user: {}", username);

            } catch (InvalidTokenException e) {
                log.error("Invalid token: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
                return;

            } catch (AccessDeniedException e) {
                log.error("Access denied: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Access denied. You do not have permission to access this resource.\"}");
                return;

            } catch (Exception e) {

                log.error("Unexpected error: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"An unexpected error occurred. Please try again later.\"}");
                return;
            }
        } else if (jwt == null && request.getRequestURI().startsWith("/api/")) {

            log.warn("No token provided for API access: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Access denied. No token provided. Please log in.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

}

