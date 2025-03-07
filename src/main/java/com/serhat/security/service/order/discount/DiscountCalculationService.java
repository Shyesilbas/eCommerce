package com.serhat.security.service.order.discount;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.DiscountCalculationResult;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.service.discountService.DiscountCodeService;
import com.serhat.security.service.giftCard.GiftCardProcessor;
import com.serhat.security.service.payment.PaymentRulesService;
import com.serhat.security.service.user.UserService;
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