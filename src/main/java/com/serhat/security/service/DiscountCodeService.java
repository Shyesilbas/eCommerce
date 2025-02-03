package com.serhat.security.service;

import com.serhat.security.dto.response.AvailableDiscountResponse;
import com.serhat.security.dto.response.ExpiredDiscountResponse;
import com.serhat.security.dto.response.UsedDiscountResponse;
import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.DiscountRate;
import com.serhat.security.exception.DiscountCodeNotFoundException;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.DiscountCodeRepository;
import com.serhat.security.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;
    private final TokenInterface tokenInterface;
    private final OrderRepository orderRepository;


    @Transactional
    public DiscountCode generateDiscountCode(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        DiscountRate discountRate = determineDiscountRate();
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(UUID.randomUUID().toString());
        discountCode.setUser(user);
        discountCode.setDiscountRate(discountRate);
        discountCode.setExpiresAt(discountCode.getExpiresAt());
        discountCode.setStatus(CouponStatus.NOT_USED);

        discountCodeRepository.save(discountCode);
        return discountCode;
    }

    public List<AvailableDiscountResponse> getAvailableDiscountCodes(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<DiscountCode> availableCodes = discountCodeRepository.findByUserAndStatus(user, CouponStatus.NOT_USED);

        if(availableCodes.isEmpty()){
            throw new DiscountCodeNotFoundException("No Discount code found for the criteria");
        }

        return availableCodes.stream()
                .map(code-> new AvailableDiscountResponse(
                        code.getId(),
                        code.getDiscountRate(),
                        code.getCode()
                ))
                .toList();


    }

    public List<UsedDiscountResponse> getUsedDiscountCodes(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<DiscountCode> usedCodes = discountCodeRepository.findByUserAndStatus(user, CouponStatus.USED);

        if (usedCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No Discount code found for the criteria");
        }

        return usedCodes.stream()
                .map(code -> orderRepository.findByDiscountCode(code)
                        .map(order -> new UsedDiscountResponse(
                                code.getId(),
                                order.getOrderId(),
                                order.getTotalPrice(),
                                code.getDiscountRate(),
                                order.getTotalDiscount(),
                                code.getCode()
                        ))
                        .orElseThrow(() -> new DiscountCodeNotFoundException("Order not found for discount code: " + code.getId()))
                )
                .toList();
    }


    public List<ExpiredDiscountResponse> getExpiredDiscountCodes(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<DiscountCode> expiredCodes = discountCodeRepository
                .findByUserAndStatusAndExpiresAtBefore(user, CouponStatus.NOT_USED, LocalDateTime.now());

        if(expiredCodes.isEmpty()){
            throw new DiscountCodeNotFoundException("No Discount code found for the criteria");
        }

        return expiredCodes.stream()
                .map(code->new ExpiredDiscountResponse(
                        code.getId(),
                        code.getDiscountRate(),
                        code.getCode()
                ))
                .toList();
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

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void markExpiredDiscountCodes() {
        List<DiscountCode> expiredCodes = discountCodeRepository.findByStatusAndExpiresAtBefore(CouponStatus.NOT_USED, LocalDateTime.now());

        if (!expiredCodes.isEmpty()) {
            expiredCodes.forEach(code->  code.setStatus(CouponStatus.EXPIRED));
            discountCodeRepository.saveAll(expiredCodes);
            log.info("{} discount codes marked as EXPIRED.", expiredCodes.size());
        }
    }
}
