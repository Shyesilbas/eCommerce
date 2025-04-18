package com.serhat.ecommerce.wallet.walletException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String s) {
        super(s);
    }
}
