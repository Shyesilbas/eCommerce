package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.AddBonusResponse;
import com.serhat.security.dto.response.BonusPointInformation;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.MembershipPlan;
import com.serhat.security.exception.AddBonusRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Map;

public interface BonusInterface {
    BonusUsageResult applyBonus(User user, OrderRequest orderRequest, BigDecimal totalPrice);
    BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice);
    BonusPointInformation bonusPointInformation(HttpServletRequest request);
     AddBonusResponse addBonus(HttpServletRequest request, AddBonusRequest bonusRequest);

    Map<MembershipPlan, BigDecimal> bonusRates = Map.of(
            MembershipPlan.VIP, BigDecimal.valueOf(0.05),
            MembershipPlan.PREMIUM, BigDecimal.valueOf(0.03),
            MembershipPlan.BASIC, BigDecimal.valueOf(0.01)
    );
}
