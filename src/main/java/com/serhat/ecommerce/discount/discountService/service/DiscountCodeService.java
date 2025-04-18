package com.serhat.ecommerce.discount.discountService.service;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;
import com.serhat.ecommerce.dto.request.OrderRequest;
import com.serhat.ecommerce.dto.response.AvailableDiscountResponse;
import com.serhat.ecommerce.dto.response.DiscountDetails;
import com.serhat.ecommerce.dto.response.ExpiredDiscountResponse;
import com.serhat.ecommerce.dto.response.UsedDiscountResponse;
import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface DiscountCodeService {
    DiscountDetails applyDiscount(OrderRequest orderRequest, BigDecimal originalPrice, User user);
    void handleDiscountCode(Order order, DiscountCode discountCode);
    DiscountCode generateDiscountCode();
    Page<AvailableDiscountResponse> getAvailableDiscountCodes(Pageable pageable);
    Page<UsedDiscountResponse> getUsedDiscountCodes(Pageable pageable);
    Page<ExpiredDiscountResponse> getExpiredDiscountCodes(Pageable pageable);
    BigDecimal getDiscountThreshold();
}