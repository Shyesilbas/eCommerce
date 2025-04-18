package com.serhat.ecommerce.discount.bonus.service;

import com.serhat.ecommerce.discount.bonus.dto.AddBonusRequest;
import com.serhat.ecommerce.dto.request.OrderRequest;
import com.serhat.ecommerce.dto.response.AddBonusResponse;
import com.serhat.ecommerce.dto.response.BonusPointInformation;
import com.serhat.ecommerce.dto.response.BonusUsageResult;
import com.serhat.ecommerce.user.userS.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonusServiceImpl implements BonusService {
    private final BonusOperationsService bonusOperationsService;
    private final BonusDetailsService bonusDetailsService;

    @Override
    public BonusUsageResult applyBonus(User user, OrderRequest orderRequest, BigDecimal totalPrice) {
        return bonusOperationsService.applyBonus(user, orderRequest, totalPrice);
    }

    @Override
    public BonusPointInformation bonusPointInformation() {
        return bonusDetailsService.bonusPointInformation();
    }

    @Override
    public BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        return bonusDetailsService.calculateBonusPoints(user, totalPrice);
    }

    @Override
    public AddBonusResponse addBonus(AddBonusRequest bonusRequest) {
        return bonusOperationsService.addBonus(bonusRequest);
    }
}