package com.serhat.ecommerce.payment.paymentException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String s) {
        super(s);
    }
}
