package com.serhat.security.interfaces.bonus;

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
    BonusPointInformation bonusPointInformation(HttpServletRequest request);
     AddBonusResponse addBonus(HttpServletRequest request, AddBonusRequest bonusRequest);
    default  void updateUserBonusPoints(User user, BigDecimal bonusPoints){
        user.setBonusPointsWon(user.getBonusPointsWon().add(bonusPoints));
        user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(bonusPoints));
    }
}
