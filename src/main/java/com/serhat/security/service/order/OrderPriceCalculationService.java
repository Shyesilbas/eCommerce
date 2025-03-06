package com.serhat.security.service.order;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.BonusCalculationResult;
import com.serhat.security.dto.response.DiscountCalculationResult;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import com.serhat.security.service.shipping.ShippingService;
import com.serhat.security.service.sCard.ShoppingCardService;
import com.serhat.security.service.order.discount.BonusCalculationService;
import com.serhat.security.service.order.discount.DiscountCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderPriceCalculationService {
    private final ShoppingCardService shoppingCardService;
    private final ShippingService shippingService;
    private final DiscountCalculationService discountCalculationService;
    private final BonusCalculationService bonusCalculationService;

    public PriceDetails calculateOrderPrice(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest) {
        BigDecimal originalPrice = shoppingCardService.cardTotal(shoppingCards);
        BigDecimal shippingFee = shippingService.calculateShippingFee(user, originalPrice);

        DiscountCalculationResult discountResult = discountCalculationService.calculateDiscounts(orderRequest, originalPrice, user);
        BonusCalculationResult bonusResult = bonusCalculationService.calculateBonus(user, orderRequest, discountResult.finalPrice());

        return PriceDetails.builder()
                .totalPrice(originalPrice)
                .originalPrice(originalPrice)
                .shippingFee(shippingFee)
                .bonusPoints(bonusResult.bonusPointsEarned())
                .discountAmount(discountResult.discountAmount())
                .finalPrice(bonusResult.finalPrice().add(shippingFee))
                .discountCode(discountResult.appliedDiscountCode())
                .bonusPointsUsed(bonusResult.bonusPointsUsed())
                .totalSaved(discountResult.discountAmount().add(bonusResult.bonusPointsUsed()))
                .build();
    }
}