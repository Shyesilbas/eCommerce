package com.serhat.ecommerce.wallet.service;

import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.wallet.entity.Wallet;

import java.math.BigDecimal;

public interface WalletValidationInterface {
    void validateLimit(Wallet wallet, BigDecimal newLimit);
    void validateDepositAmount(BigDecimal amount);
    void checkAmountAndLimit(Wallet wallet, BigDecimal amount);
    void checkIfDepositValid(Wallet wallet, BigDecimal amount);
    void hasUserHaveWallet(User user);
}
