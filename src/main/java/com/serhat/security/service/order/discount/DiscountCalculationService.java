package com.serhat.security.service.order.discount;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.DiscountCalculationResult;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.interfaces.DiscountInterface;
import com.serhat.security.service.giftCard.GiftCardService;
import com.serhat.security.service.payment.PaymentRulesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DiscountCalculationService {
    private final DiscountInterface discountInterface;
    private final GiftCardService giftCardService;
    private final PaymentRulesService paymentRulesService;

    public DiscountCalculationResult calculateDiscounts(OrderRequest orderRequest, BigDecimal originalPrice, User user) {
        paymentRulesService.validateDiscountRules(orderRequest);

        DiscountDetails discountDetails = discountInterface.applyDiscount(orderRequest, originalPrice, user);
        BigDecimal priceAfterDiscount = originalPrice.subtract(discountDetails.discountAmount());

        GiftCard giftCard = giftCardService.applyGiftCard(orderRequest, priceAfterDiscount);
        BigDecimal finalPrice = giftCard != null
                ? priceAfterDiscount.subtract(giftCard.getGiftAmount().getAmount())
                : priceAfterDiscount;

        return new DiscountCalculationResult(finalPrice, discountDetails.discountAmount(), discountDetails.discountCode());
    }
}
