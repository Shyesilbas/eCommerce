package com.serhat.ecommerce.payment.paymentException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String s) {
        super(s);
    }
}
