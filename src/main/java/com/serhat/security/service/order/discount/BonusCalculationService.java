package com.serhat.security.service.order.discount;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.BonusCalculationResult;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.entity.User;
import com.serhat.security.service.BonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BonusCalculationService {
    private final BonusService bonusService;

    public BonusCalculationResult calculateBonus(User user, OrderRequest request, BigDecimal priceAfterDiscounts) {
        BigDecimal bonusPointsEarned = bonusService.calculateBonusPoints(user, priceAfterDiscounts);
        BonusUsageResult bonusUsage = bonusService.applyBonus(user, request, priceAfterDiscounts);

        return new BonusCalculationResult(
                bonusUsage.updatedTotalPrice(),
                bonusPointsEarned,
                bonusUsage.bonusPointsUsed()
        );
    }
}
