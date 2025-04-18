package com.serhat.ecommerce.discount.discountService.service;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;
import com.serhat.ecommerce.discount.discountService.repository.DiscountCodeRepository;
import com.serhat.ecommerce.enums.CouponStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class discountCodeStatusMarker {
    private final DiscountCodeRepository discountCodeRepository;
    @PostConstruct
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
