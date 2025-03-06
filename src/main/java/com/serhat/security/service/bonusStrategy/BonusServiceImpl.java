package com.serhat.security.service.bonusStrategy;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.AddBonusResponse;
import com.serhat.security.dto.response.BonusPointInformation;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.entity.User;
import com.serhat.security.dto.request.AddBonusRequest;
import com.serhat.security.exception.InvalidAmountException;
import com.serhat.security.exception.NoBonusPointsException;
import com.serhat.security.component.mapper.UserMapper;
import com.serhat.security.repository.UserRepository;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BonusServiceImpl implements BonusService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BonusStrategyFactory bonusStrategyFactory;
    private final UserService userService;


    @Transactional
    @Override
    public BonusUsageResult applyBonus(User user, OrderRequest orderRequest, BigDecimal totalPrice) {
        BigDecimal bonusPointsUsed = BigDecimal.ZERO;

        if (orderRequest.useBonus() != null && orderRequest.useBonus()) {
            BigDecimal availableBonusPoints = user.getCurrentBonusPoints();

            if (availableBonusPoints.compareTo(BigDecimal.ZERO) > 0) {
                bonusPointsUsed = availableBonusPoints.min(totalPrice);
                user.setCurrentBonusPoints(availableBonusPoints.subtract(bonusPointsUsed));
            } else {
                throw new NoBonusPointsException("No bonus points found");
            }
        }
        return new BonusUsageResult(totalPrice, bonusPointsUsed);
    }

    @Transactional
    @Override
    public void updateUserBonusPoints(User user, BigDecimal bonusPoints) {
        user.setBonusPointsWon(user.getBonusPointsWon().add(bonusPoints));
        user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(bonusPoints));
    }

    public BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        BonusStrategy strategy = bonusStrategyFactory.getBonusStrategy(user);
        return strategy.calculateBonusPoints(user, totalPrice);
    }

    @Override
    public BonusPointInformation bonusPointInformation(){
        User user = userService.getAuthenticatedUser();
        return new BonusPointInformation(
                user.getBonusPointsWon(),
                user.getCurrentBonusPoints()
        );
    }

    @CachePut(value = "userInfoCache",key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    @Transactional
    @Override
    public AddBonusResponse addBonus(AddBonusRequest bonusRequest){
        User user = userService.getAuthenticatedUser();
        if(bonusRequest.amount().compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidAmountException("Amount must be positive!");
        }
        updateUserBonusPoints(user,bonusRequest.amount());
        userRepository.save(user);
        return userMapper.toAddBonusResponse(user,bonusRequest.amount());
    }
}
