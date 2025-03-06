package com.serhat.security.service.bonusStrategy;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.AddBonusResponse;
import com.serhat.security.dto.response.BonusPointInformation;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.entity.User;
import com.serhat.security.dto.request.AddBonusRequest;

import java.math.BigDecimal;

public interface BonusService {
    BonusUsageResult applyBonus(User user, OrderRequest orderRequest, BigDecimal totalPrice);
    BonusPointInformation bonusPointInformation();
    BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice);
     AddBonusResponse addBonus( AddBonusRequest bonusRequest);
      void updateUserBonusPoints(User user, BigDecimal bonusPoints);

}
