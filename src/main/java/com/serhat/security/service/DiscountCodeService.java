package com.serhat.security.service;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.DiscountRate;
import com.serhat.security.exception.NoEligibleDiscountException;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.DiscountCodeRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;
    private final TokenInterface tokenInterface;


    public DiscountCode generateDiscountCode(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        DiscountRate discountRate = determineDiscountRate();
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(UUID.randomUUID().toString());
        discountCode.setUser(user);
        discountCode.setDiscountRate(discountRate);
        discountCode.setExpiresAt(discountCode.getExpiresAt());

        discountCodeRepository.save(discountCode);
        return discountCode;
    }

    private DiscountRate determineDiscountRate() {
        double random = Math.random();
        if (random < 0.5) {
            return DiscountRate.TEN_PERCENT;
        } else if (random < 0.8) {
            return DiscountRate.TWENTY_PERCENT;
        } else {
            return DiscountRate.THIRTY_PERCENT;
        }
    }
}
