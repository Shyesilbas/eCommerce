package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;

public interface PaymentRuleServiceInterface {
    void validateDiscountRules(OrderRequest orderRequest);
}
