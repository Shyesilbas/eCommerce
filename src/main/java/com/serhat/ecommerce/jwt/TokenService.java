package com.serhat.ecommerce.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    public List<Token> getAllTokens() {
        return tokenRepository.findAll();
    }
    public Optional<Token> getToken(String username){
        return tokenRepository.findByUsername(username);
    }

    public void saveToken(Token token) {
        tokenRepository.save(token);
    }
}
