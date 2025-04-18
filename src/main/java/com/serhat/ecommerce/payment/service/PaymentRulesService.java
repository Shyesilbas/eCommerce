package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.dto.request.OrderRequest;
import com.serhat.ecommerce.order.orderException.InvalidOrderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class PaymentRulesService implements PaymentRuleServiceInterface {

    @Override
    public void validateDiscountRules(OrderRequest orderRequest){
        if (orderRequest.discountId() != null && orderRequest.giftCardId() != null) {
            throw new InvalidOrderException("Cannot use both discount code and gift card in the same order.");
        }
    }
}
