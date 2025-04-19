package com.serhat.ecommerce.discount.bonus.service;

import com.serhat.ecommerce.order.dto.request.OrderRequest;
import com.serhat.ecommerce.discount.dto.response.AddBonusResponse;
import com.serhat.ecommerce.discount.dto.response.BonusPointInformation;
import com.serhat.ecommerce.discount.dto.response.BonusUsageResult;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.discount.bonus.dto.AddBonusRequest;

import java.math.BigDecimal;

public interface BonusService {
    BonusUsageResult applyBonus(User user, OrderRequest orderRequest, BigDecimal totalPrice);
    BonusPointInformation bonusPointInformation();
    BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice);
     AddBonusResponse addBonus( AddBonusRequest bonusRequest);

}
