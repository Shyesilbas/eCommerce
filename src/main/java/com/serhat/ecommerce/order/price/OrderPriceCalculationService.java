package com.serhat.ecommerce.order.price;

import com.serhat.ecommerce.order.dto.request.OrderRequest;
import com.serhat.ecommerce.discount.dto.response.BonusCalculationResult;
import com.serhat.ecommerce.discount.dto.response.DiscountCalculationResult;
import com.serhat.ecommerce.product.dto.PriceDetails;
import com.serhat.ecommerce.order.discount.BonusCalculationService;
import com.serhat.ecommerce.order.discount.DiscountCalculationService;
import com.serhat.ecommerce.sCard.entity.ShoppingCard;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.shipping.service.ShippingService;
import com.serhat.ecommerce.sCard.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderPriceCalculationService {
    private final ShoppingCartService shoppingCartService;
    private final ShippingService shippingService;
    private final DiscountCalculationService discountCalculationService;
    private final BonusCalculationService bonusCalculationService;

    public PriceDetails calculateOrderPrice(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest) {
        BigDecimal originalPrice = shoppingCartService.cardTotal(shoppingCards);
        BigDecimal shippingFee = shippingService.calculateShippingFee(user, originalPrice);

        DiscountCalculationResult discountResult = discountCalculationService.calculateDiscounts(orderRequest, originalPrice);
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