package com.serhat.ecommerce.wallet.mapper;

import com.serhat.ecommerce.wallet.dto.WalletRequest;
import com.serhat.ecommerce.wallet.dto.DepositSuccessfulResponse;
import com.serhat.ecommerce.wallet.dto.WalletCreatedResponse;
import com.serhat.ecommerce.wallet.dto.WalletInfoResponse;
import com.serhat.ecommerce.wallet.dto.WalletLimitUpdateResponse;
import com.serhat.ecommerce.wallet.entity.Wallet;
import com.serhat.ecommerce.payment.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WalletMapper {

    public Wallet toWallet(WalletRequest walletRequest) {
        Wallet wallet = new Wallet();
        wallet.setWalletName(walletRequest.walletName());
        wallet.setDescription(walletRequest.description());
        wallet.setWalletLimit(walletRequest.limit());
        wallet.setWalletPin(walletRequest.walletPin());
        wallet.setBalance(BigDecimal.ZERO);
        return wallet;
    }


    public WalletCreatedResponse toWalletCreatedResponse(Wallet wallet) {
        return new WalletCreatedResponse(
                wallet.getWalletId(),
                wallet.getWalletName(),
                wallet.getBalance(),
                wallet.getDescription()
        );
    }

    public WalletLimitUpdateResponse toWalletLimitUpdateResponse(Wallet wallet , BigDecimal newLimit){
        return new WalletLimitUpdateResponse(
                "Limit Updated Successfully",
                newLimit,
                LocalDateTime.now()
        );
    }

    public DepositSuccessfulResponse toDepositSuccessfulResponse(BigDecimal amount, Wallet wallet){
        return new DepositSuccessfulResponse(
                amount,
                wallet.getBalance(),
                LocalDateTime.now(),
                TransactionType.DEPOSIT
        );
    }


    public WalletInfoResponse toWalletInfoResponse(Wallet wallet) {
        return new WalletInfoResponse(
                wallet.getDescription(),
                wallet.getWalletName(),
                wallet.getWalletLimit(),
                wallet.getBalance()
        );
    }
}