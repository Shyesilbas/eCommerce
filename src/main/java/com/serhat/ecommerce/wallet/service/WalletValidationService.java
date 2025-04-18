package com.serhat.ecommerce.wallet.service;

import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.wallet.repository.WalletRepository;
import com.serhat.ecommerce.wallet.entity.Wallet;
import com.serhat.ecommerce.wallet.walletException.AlreadyHasWalletException;
import com.serhat.ecommerce.payment.paymentException.InvalidAmountException;
import com.serhat.ecommerce.wallet.walletException.LimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletValidationService implements WalletValidationInterface {
    private final WalletRepository walletRepository;

    @Override
    public void validateLimit(Wallet wallet, BigDecimal newLimit) {
        if (newLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Invalid limit request!");
        }
        if (wallet.getBalance().compareTo(newLimit) > 0) {
            throw new LimitExceededException("Your current balance exceeds the new limit. Please adjust your balance before updating the limit.");
        }
    }
    @Override
    public void validateDepositAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }
    }

    @Override
    public void checkAmountAndLimit(Wallet wallet, BigDecimal amount) {
        BigDecimal limit = wallet.getWalletLimit();
        BigDecimal balanceAfterDepositRequest = wallet.getBalance().add(amount);

        if (balanceAfterDepositRequest.compareTo(limit) > 0) {
            throw new LimitExceededException("After deposit, you will exceed your limit. Update your limit.");
        }
    }

    @Override
    public void checkIfDepositValid(Wallet wallet , BigDecimal amount){
        validateDepositAmount(amount);
        checkAmountAndLimit(wallet, amount);
    }

    @Override
    public void hasUserHaveWallet(User user){
        boolean hasUserHaveWallet = walletRepository.findByUser_UserId(user.getUserId()).isPresent();
        if(hasUserHaveWallet){
            throw new AlreadyHasWalletException("You can only have 1 wallet currently active.");
        }
    }

}
