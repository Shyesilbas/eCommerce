package com.serhat.ecommerce.discount.bonus.service;

import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.payment.paymentException.InvalidAmountException;
import com.serhat.ecommerce.discount.discountException.NoBonusPointsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BonusValidationService {
    public void validateBonusAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive!");
        }
    }

    public void validateAvailableBonusPoints(User user, BigDecimal totalPrice) {
        BigDecimal availableBonusPoints = user.getCurrentBonusPoints();
        if (availableBonusPoints.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NoBonusPointsException("No bonus points available to use!");
        }
    }
}