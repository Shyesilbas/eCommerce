package com.serhat.security.service.discountService;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.AvailableDiscountResponse;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.dto.response.ExpiredDiscountResponse;
import com.serhat.security.dto.response.UsedDiscountResponse;
import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface DiscountCodeService{
    DiscountDetails applyDiscount(OrderRequest orderRequest, BigDecimal originalPrice, User user);

    void handleDiscountCode(HttpServletRequest request, Order order, DiscountCode discountCode);
    DiscountCode generateDiscountCode(HttpServletRequest request);
    Page<AvailableDiscountResponse> getAvailableDiscountCodes(HttpServletRequest request, Pageable pageable);
    Page<UsedDiscountResponse> getUsedDiscountCodes(HttpServletRequest request, Pageable pageable);
    Page<ExpiredDiscountResponse> getExpiredDiscountCodes(HttpServletRequest request, Pageable pageable);
    BigDecimal calculateDiscountAmount(BigDecimal originalPrice, DiscountCode discountCode);
    void validateDiscountCode(DiscountCode discountCode , User user);
    void updateCouponStatusToUsed( DiscountCode discountCode);
    void generateDiscountCodeIfOrderThresholdExceeded(Order order , HttpServletRequest request);
    BigDecimal getDiscountThreshold();

}
