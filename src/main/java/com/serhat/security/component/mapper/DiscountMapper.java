package com.serhat.security.component.mapper;

import com.serhat.security.dto.response.AvailableDiscountResponse;
import com.serhat.security.dto.response.ExpiredDiscountResponse;
import com.serhat.security.dto.response.UsedDiscountResponse;
import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.DiscountRate;
import com.serhat.security.exception.DiscountCodeNotFoundException;
import com.serhat.security.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DiscountMapper {

    private final OrderRepository orderRepository;

    public AvailableDiscountResponse toAvailableDiscountResponse(DiscountCode discountCode) {
        return new AvailableDiscountResponse(
                discountCode.getId(),
                discountCode.getDiscountRate(),
                discountCode.getCode()
        );
    }

    public DiscountCode createDiscountCode(User user , DiscountRate discountRate){
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(UUID.randomUUID().toString());
        discountCode.setUser(user);
        discountCode.setDiscountRate(discountRate);
        discountCode.setExpiresAt(LocalDateTime.now().plusDays(30));
        discountCode.setStatus(CouponStatus.NOT_USED);
        return discountCode;
    }

    public UsedDiscountResponse toUsedDiscountResponse(DiscountCode discountCode) {
        Order order = orderRepository.findByDiscountCode(discountCode)
                .orElseThrow(() -> new DiscountCodeNotFoundException("Order not found for discount code: " + discountCode.getId()));

        return new UsedDiscountResponse(
                discountCode.getId(),
                order.getOrderId(),
                order.getTotalPrice(),
                discountCode.getDiscountRate(),
                order.getTotalDiscount(),
                discountCode.getCode()
        );
    }

    public ExpiredDiscountResponse toExpiredDiscountResponse(DiscountCode discountCode) {
        return new ExpiredDiscountResponse(
                discountCode.getId(),
                discountCode.getDiscountRate(),
                discountCode.getCode()
        );
    }


}
