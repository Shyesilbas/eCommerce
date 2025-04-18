package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.dto.request.OrderRequest;

public interface PaymentRuleServiceInterface {
    void validateDiscountRules(OrderRequest orderRequest);
}
