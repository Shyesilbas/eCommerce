package com.serhat.security.interfaces;

import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;

import java.math.BigDecimal;

public interface WalletValidationInterface {
    void validateLimit(Wallet wallet, BigDecimal newLimit);
    void validateDepositAmount(BigDecimal amount);
    void checkAmountAndLimit(Wallet wallet, BigDecimal amount);
    void checkIfDepositValid(Wallet wallet, BigDecimal amount);
    void hasUserHaveWallet(User user);
}
