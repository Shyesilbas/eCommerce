package com.serhat.security.service.bonus;

import com.serhat.security.dto.request.AddBonusRequest;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.AddBonusResponse;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.entity.User;
import com.serhat.security.component.mapper.UserMapper;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonusOperationsService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final BonusValidationService bonusValidationService;

    @Transactional
    public BonusUsageResult applyBonus(User user, OrderRequest orderRequest, BigDecimal totalPrice) {
        BigDecimal bonusPointsUsed = BigDecimal.ZERO;

        if (orderRequest.useBonus() != null && orderRequest.useBonus()) {
            bonusValidationService.validateAvailableBonusPoints(user, totalPrice);
            BigDecimal availableBonusPoints = user.getCurrentBonusPoints();
            bonusPointsUsed = availableBonusPoints.min(totalPrice);
            userService.updateUserBonusPoints(user, bonusPointsUsed.negate());
            log.info("Bonus applied by user: {} with amount: {}", user.getUsername(), bonusPointsUsed);
        }

        return new BonusUsageResult(totalPrice.subtract(bonusPointsUsed), bonusPointsUsed);
    }

    @CachePut(value = "userInfoCache", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    @Transactional
    public AddBonusResponse addBonus(AddBonusRequest bonusRequest) {
        User user = userService.getAuthenticatedUser();
        bonusValidationService.validateBonusAmount(bonusRequest.amount());
        userService.updateUserBonusPoints(user, bonusRequest.amount());
        log.info("Bonus added for user: {} with amount: {}", user.getUsername(), bonusRequest.amount());
        return userMapper.toAddBonusResponse(user, bonusRequest.amount());
    }
}