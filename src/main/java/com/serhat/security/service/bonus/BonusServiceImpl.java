package com.serhat.security.service.bonus;

import com.serhat.security.dto.request.AddBonusRequest;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.AddBonusResponse;
import com.serhat.security.dto.response.BonusPointInformation;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.entity.User;
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