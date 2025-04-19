package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.order.dto.request.OrderRequest;

public interface PaymentRuleServiceInterface {
    void validateDiscountRules(OrderRequest orderRequest);
}
