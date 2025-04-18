package com.serhat.ecommerce.payment.paymentException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidPaymentMethodException extends RuntimeException {
    public InvalidPaymentMethodException(String s) {
        super(s);
    }
}
