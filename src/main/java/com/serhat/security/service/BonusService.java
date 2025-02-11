package com.serhat.security.service;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.AddBonusResponse;
import com.serhat.security.dto.response.BonusPointInformation;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.entity.User;
import com.serhat.security.exception.AddBonusRequest;
import com.serhat.security.exception.InvalidAmountException;
import com.serhat.security.exception.NoBonusPointsException;
import com.serhat.security.interfaces.bonus.BonusInterface;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.interfaces.bonus.BonusStrategy;
import com.serhat.security.mapper.UserMapper;
import com.serhat.security.repository.UserRepository;
import com.serhat.security.service.bonusStrategy.BonusStrategyFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BonusService implements BonusInterface {
    private final TokenInterface tokenInterface;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BonusStrategyFactory bonusStrategyFactory;


    @Override
    public BonusUsageResult applyBonus(User user, OrderRequest orderRequest, BigDecimal totalPrice) {
        BigDecimal bonusPointsUsed = BigDecimal.ZERO;

        if (orderRequest.useBonus() != null && orderRequest.useBonus()) {
            BigDecimal availableBonusPoints = user.getCurrentBonusPoints();

            if (availableBonusPoints.compareTo(BigDecimal.ZERO) > 0) {
                bonusPointsUsed = availableBonusPoints.min(totalPrice);
                totalPrice = totalPrice.subtract(bonusPointsUsed);

                user.setCurrentBonusPoints(availableBonusPoints.subtract(bonusPointsUsed));
            } else {
                throw new NoBonusPointsException("No bonus points found");
            }
        }
        return new BonusUsageResult(totalPrice, bonusPointsUsed);
    }

    @Override
    public void updateUserBonusPoints(User user, BigDecimal bonusPoints) {
        BonusInterface.super.updateUserBonusPoints(user, bonusPoints);
    }

    public BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        BonusStrategy strategy = bonusStrategyFactory.getBonusStrategy(user);
        return strategy.calculateBonusPoints(user, totalPrice);
    }

    @Override
    public BonusPointInformation bonusPointInformation(HttpServletRequest request){
        User user = tokenInterface.getUserFromToken(request);
        return new BonusPointInformation(
                user.getBonusPointsWon(),
                user.getCurrentBonusPoints()
        );
    }

   // @CacheEvict(value = "userInfoCache", key = "#request.userPrincipal.name")
    @Transactional
    @Override
    public AddBonusResponse addBonus(HttpServletRequest request, AddBonusRequest bonusRequest){
        User user = tokenInterface.getUserFromToken(request);
        if(bonusRequest.amount().compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidAmountException("Amount must be positive!");
        }
        updateUserBonusPoints(user,bonusRequest.amount());
        userRepository.save(user);
        return userMapper.toAddBonusResponse(user,bonusRequest.amount());
    }
}
