package com.serhat.ecommerce.order.discount;

import com.serhat.ecommerce.order.dto.request.OrderRequest;
import com.serhat.ecommerce.discount.dto.response.DiscountCalculationResult;
import com.serhat.ecommerce.discount.dto.response.DiscountDetails;
import com.serhat.ecommerce.discount.giftCard.entity.GiftCard;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.discount.discountService.service.DiscountCodeService;
import com.serhat.ecommerce.discount.giftCard.service.GiftCardProcessor;
import com.serhat.ecommerce.payment.service.PaymentRulesService;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DiscountCalculationService {
    private final DiscountCodeService discountCodeService;
    private final GiftCardProcessor giftCardProcessor;
    private final PaymentRulesService paymentRulesService;
    private final UserService userService;

    public DiscountCalculationResult calculateDiscounts(OrderRequest orderRequest, BigDecimal originalPrice) {
        paymentRulesService.validateDiscountRules(orderRequest);

        User user = userService.getAuthenticatedUser();
        DiscountDetails discountDetails = discountCodeService.applyDiscount(orderRequest, originalPrice, user);
        BigDecimal priceAfterDiscount = originalPrice.subtract(discountDetails.discountAmount());

        GiftCard giftCard = giftCardProcessor.applyGiftCard(orderRequest, priceAfterDiscount);
        BigDecimal finalPrice = giftCard != null
                ? priceAfterDiscount.subtract(giftCard.getGiftAmount().getAmount())
                : priceAfterDiscount;

        return new DiscountCalculationResult(finalPrice, discountDetails.discountAmount(), discountDetails.discountCode());
    }
}