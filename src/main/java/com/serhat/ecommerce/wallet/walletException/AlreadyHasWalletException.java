package com.serhat.ecommerce.wallet.walletException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlreadyHasWalletException extends RuntimeException {
    public AlreadyHasWalletException(String s) {
        super(s);
    }
}
