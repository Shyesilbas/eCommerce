package com.serhat.security.service.payment;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.exception.InvalidOrderException;
import com.serhat.security.interfaces.PaymentRuleServiceInterface;
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
