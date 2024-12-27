package com.serhat.security.service;

import com.serhat.security.entity.Token;
import com.serhat.security.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Optional<Token> getToken(String username){
        return tokenRepository.findByUsername(username);
    }
}
