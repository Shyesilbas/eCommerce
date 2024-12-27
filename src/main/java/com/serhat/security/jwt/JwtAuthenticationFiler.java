package com.serhat.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFiler extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.getTokenFromRequest(request);
        if(token != null && jwtUtil.validateToken(token)){
            String username = jwtUtil.getUsernameFromToken(token);
            SecurityContextHolder.getContext().setAuthentication(jwtUtil.getAuthentication(username));
        }
        filterChain.doFilter(request,response);
    }
}
