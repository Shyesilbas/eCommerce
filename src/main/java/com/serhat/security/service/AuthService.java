package com.serhat.security.service;

import com.serhat.security.entity.Token;
import com.serhat.security.entity.enums.TokenStatus;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    public String login(String username){
        String token = jwtUtil.generateToken(username);
        Token newToken = new Token();
        newToken.setUsername(username);
        newToken.setToken(token);
        newToken.setCreatedAt(new Date());
        newToken.setExpiresAt(new Date(System.currentTimeMillis() + expirationTime));
        newToken.setTokenStatus(TokenStatus.ACTIVE);
        tokenRepository.save(newToken);
        return token;
    }
}
