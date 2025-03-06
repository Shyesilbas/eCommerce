package com.serhat.security.service.discountService;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.AvailableDiscountResponse;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.dto.response.ExpiredDiscountResponse;
import com.serhat.security.dto.response.UsedDiscountResponse;
import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.DiscountRate;
import com.serhat.security.exception.DiscountCodeNotFoundException;
import com.serhat.security.jwt.JwtOperations;
import com.serhat.security.jwt.TokenInterfaceImpl;
import com.serhat.security.component.mapper.DiscountMapper;
import com.serhat.security.repository.DiscountCodeRepository;
import com.serhat.security.repository.UserRepository;
import com.serhat.security.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
@Slf4j
@Getter
public class DiscountCodeServiceImpl extends TokenInterfaceImpl implements DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;
    private final DiscountMapper discountMapper;
    private final DiscountValidationInterface discountValidationInterface;
    private final UserService userService;

    public DiscountCodeServiceImpl(@Qualifier("jwtValidator")
                                   JwtOperations jwtOperations,
                                   UserRepository userRepository,
                                   DiscountCodeRepository discountCodeRepository,
                                   DiscountMapper discountMapper, UserService userService,DiscountValidationInterface discountValidationInterface) {
        super(jwtOperations, userRepository);
        this.discountCodeRepository = discountCodeRepository;
        this.discountMapper = discountMapper;
        this.discountValidationInterface = discountValidationInterface;
        this.userService=userService;
    }

    @Value("${discount.code.threshold}")
    private BigDecimal discountThreshold;

    public User getUser(HttpServletRequest request){
        return getUserFromToken(request);
    }

    @Override
    public DiscountCode generateDiscountCode() {
        User user = userService.getAuthenticatedUser();
        DiscountRate discountRate = determineDiscountRate();
        DiscountCode discountCode = discountMapper.createDiscountCode(user,discountRate);
        discountCodeRepository.save(discountCode);
        return discountCode;
    }

    @Override
    public Page<AvailableDiscountResponse> getAvailableDiscountCodes(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<DiscountCode> availableCodes = discountCodeRepository.findByUserAndStatus(user, CouponStatus.NOT_USED, pageable);

        if (availableCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No available discount codes found.");
        }

        return availableCodes.map(discountMapper::toAvailableDiscountResponse);
    }

    @Override
    public Page<UsedDiscountResponse> getUsedDiscountCodes(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<DiscountCode> usedCodes = discountCodeRepository.findByUserAndStatus(user, CouponStatus.USED, pageable);

        if (usedCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No used discount codes found.");
        }

        return usedCodes.map(discountMapper::toUsedDiscountResponse);
    }

    @Override
    public Page<ExpiredDiscountResponse> getExpiredDiscountCodes(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<DiscountCode> expiredCodes = discountCodeRepository
                .findByUserAndStatusAndExpiresAtBefore(user, CouponStatus.NOT_USED, LocalDateTime.now(), pageable);

        if (expiredCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No expired discount codes found.");
        }

        return expiredCodes.map(discountMapper::toExpiredDiscountResponse);
    }

    @Override
    public BigDecimal calculateDiscountAmount(BigDecimal originalPrice, DiscountCode discountCode) {
        return originalPrice
                .multiply(BigDecimal.valueOf(discountCode.getDiscountRate().getPercentage() / 100.0));
    }
    private DiscountRate determineDiscountRate() {
        double random = Math.random();
        if (random < 0.5) {
            return DiscountRate.TEN_PERCENT;
        } else if (random < 0.8) {
            return DiscountRate.TWENTY_PERCENT;
        } else {
            return DiscountRate.THIRTY_PERCENT;
        }
    }
    @Override
    public void validateDiscountCode(DiscountCode discountCode , User user){
        discountValidationInterface.validateDiscountCode(discountCode, user);
    }

    public DiscountCode findById(Long id){
        return discountValidationInterface.findById(id);
    }

    @Override
    public DiscountDetails applyDiscount(OrderRequest orderRequest, BigDecimal originalPrice, User user) {
        if (orderRequest.discountId() == null) {
            return new DiscountDetails(BigDecimal.ZERO, null);
        }
        try {
            DiscountCode discountCode = findById(orderRequest.discountId());
            validateDiscountCode(discountCode, user);
            BigDecimal discountAmount = calculateDiscountAmount(originalPrice, discountCode);
            return new DiscountDetails(discountAmount, discountCode);
        } catch (DiscountCodeNotFoundException e) {
            log.warn("Discount code not found: {}", orderRequest.discountId());
            return new DiscountDetails(BigDecimal.ZERO, null);
        }
    }
    @Override
    public void generateDiscountCodeIfOrderThresholdExceeded(Order order){
        if (order != null && order.getTotalPrice().compareTo(getDiscountThreshold()) >= 0) {
            generateDiscountCode();
        }
    }
    @Override
    public void updateCouponStatusToUsed( DiscountCode discountCode) {
        if (discountCode != null) {
            discountCode.setStatus(CouponStatus.USED);
        }
    }

    @Override
    public void handleDiscountCode(Order order , DiscountCode discountCode){
        generateDiscountCodeIfOrderThresholdExceeded(order);
        updateCouponStatusToUsed(discountCode);
    }
}

