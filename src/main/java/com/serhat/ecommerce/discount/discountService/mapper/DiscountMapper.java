package com.serhat.ecommerce.discount.discountService.mapper;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;
import com.serhat.ecommerce.discount.dto.response.AvailableDiscountResponse;
import com.serhat.ecommerce.discount.dto.response.ExpiredDiscountResponse;
import com.serhat.ecommerce.discount.dto.response.UsedDiscountResponse;
import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.discount.enums.CouponStatus;
import com.serhat.ecommerce.discount.enums.DiscountRate;
import com.serhat.ecommerce.discount.discountException.DiscountCodeNotFoundException;
import com.serhat.ecommerce.order.repo.OrderRepository;
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
