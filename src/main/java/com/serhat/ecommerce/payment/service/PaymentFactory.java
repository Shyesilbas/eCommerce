package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.enums.PaymentMethod;
import com.serhat.ecommerce.payment.entity.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFactory {
    private final EWalletPaymentService eWalletPaymentService;
    private final OnlinePaymentService onlinePaymentService;

    public PaymentService<?> getPaymentService(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case BANK_TRANSFER -> null;
            case CREDIT_CARD_ON_DELIVERY -> null;
            case CASH_ON_DELIVERY -> null;
            case ONLINE -> onlinePaymentService;
            case E_WALLET -> eWalletPaymentService;
        };
    }
}
