package com.serhat.ecommerce.wallet.walletException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String s) {
        super(s);
    }
}
