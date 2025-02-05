package com.serhat.security.jwt;

import com.serhat.security.entity.User;
import com.serhat.security.exception.InvalidTokenException;
import com.serhat.security.service.TokenBlacklistService;
import com.serhat.security.service.UserDetailsServiceImpl;
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
                    response.getWriter().write("Token is blacklisted");
                    return;
                }

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                User user = (User) userDetails;

                if (jwtUtil.validateToken(jwt, user)) {
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
                } else {
                    log.warn("Invalid token for user: {}", username);
                    throw new InvalidTokenException("Invalid or expired token");
                }
            } catch (Exception e) {
                log.error("Error processing JWT token", e);
                throw new InvalidTokenException("Authentication failed");
            }
        }

        filterChain.doFilter(request, response);
    }
}
