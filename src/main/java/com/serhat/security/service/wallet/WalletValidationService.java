package com.serhat.security.service.wallet;

import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.exception.AlreadyHasWalletException;
import com.serhat.security.exception.InvalidAmountException;
import com.serhat.security.exception.LimitExceededException;
import com.serhat.security.interfaces.WalletValidationInterface;
import com.serhat.security.repository.WalletRepository;
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
