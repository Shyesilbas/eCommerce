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
import com.serhat.security.mapper.DiscountMapper;
import com.serhat.security.repository.DiscountCodeRepository;
import com.serhat.security.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;
    private final TokenInterface tokenInterface;
    private final DiscountMapper discountResponseMapper;

    @Value("${discount.code.threshold}")
    private BigDecimal discountThreshold;

    @Transactional
    public DiscountCode generateDiscountCode(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        DiscountRate discountRate = determineDiscountRate();
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(UUID.randomUUID().toString());
        discountCode.setUser(user);
        discountCode.setDiscountRate(discountRate);
        discountCode.setExpiresAt(LocalDateTime.now().plusDays(30));
        discountCode.setStatus(CouponStatus.NOT_USED);

        discountCodeRepository.save(discountCode);
        return discountCode;
    }

    public Page<AvailableDiscountResponse> getAvailableDiscountCodes(HttpServletRequest request, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(request);
        Page<DiscountCode> availableCodes = discountCodeRepository.findByUserAndStatus(user, CouponStatus.NOT_USED, pageable);

        if (availableCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No available discount codes found.");
        }

        return availableCodes.map(discountResponseMapper::toAvailableDiscountResponse);
    }

    public Page<UsedDiscountResponse> getUsedDiscountCodes(HttpServletRequest request, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(request);
        Page<DiscountCode> usedCodes = discountCodeRepository.findByUserAndStatus(user, CouponStatus.USED, pageable);

        if (usedCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No used discount codes found.");
        }

        return usedCodes.map(discountResponseMapper::toUsedDiscountResponse);
    }

    public Page<ExpiredDiscountResponse> getExpiredDiscountCodes(HttpServletRequest request, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(request);
        Page<DiscountCode> expiredCodes = discountCodeRepository
                .findByUserAndStatusAndExpiresAtBefore(user, CouponStatus.NOT_USED, LocalDateTime.now(), pageable);

        if (expiredCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No expired discount codes found.");
        }

        return expiredCodes.map(discountResponseMapper::toExpiredDiscountResponse);
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
        Pageable pageable = PageRequest.of(0, 20);
        boolean hasMorePages = true;

        while (hasMorePages) {
            Page<DiscountCode> expiredCodesPage = discountCodeRepository
                    .findByStatusAndExpiresAtBefore(CouponStatus.NOT_USED, LocalDateTime.now(), pageable);

            if (expiredCodesPage.isEmpty()) {
                hasMorePages = false;
            } else {
                expiredCodesPage.getContent().forEach(code -> code.setStatus(CouponStatus.EXPIRED));
                discountCodeRepository.saveAll(expiredCodesPage.getContent());

                log.info("{} discount codes marked as EXPIRED.", expiredCodesPage.getContent().size());

                pageable = expiredCodesPage.nextPageable();
            }
        }
    }

}

