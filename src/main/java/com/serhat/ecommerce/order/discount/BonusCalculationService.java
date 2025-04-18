package com.serhat.ecommerce.order.discount;

import com.serhat.ecommerce.dto.request.OrderRequest;
import com.serhat.ecommerce.dto.response.BonusCalculationResult;
import com.serhat.ecommerce.dto.response.BonusUsageResult;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.discount.bonus.service.BonusService;
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
