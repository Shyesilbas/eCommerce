package com.serhat.security.component;

import com.serhat.security.entity.enums.TokenStatus;
import com.serhat.security.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenStatusChecker {
    private final TokenService tokenService;

    @Scheduled(fixedRate = 60000)
    public void checkTokenStatus(String username){
        tokenService.getToken(username).ifPresent(
                token -> {
                    if(token.getExpiresAt().before(new Date())){
                    token.setTokenStatus(TokenStatus.EXPIRED);
                   }
                }
        );
    }
}
