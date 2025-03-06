package com.serhat.security.service.payment;

import com.serhat.security.dto.request.OrderRequest;

public interface PaymentRuleServiceInterface {
    void validateDiscountRules(OrderRequest orderRequest);
}
