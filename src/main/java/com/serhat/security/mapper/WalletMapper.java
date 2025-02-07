package com.serhat.security.mapper;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.DepositSuccessfulResponse;
import com.serhat.security.dto.response.WalletCreatedResponse;
import com.serhat.security.dto.response.WalletInfoResponse;
import com.serhat.security.dto.response.WalletLimitUpdateResponse;
import com.serhat.security.entity.Wallet;
import com.serhat.security.entity.enums.TransactionType;
import com.serhat.security.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WalletMapper {
    private final WalletRepository walletRepository;

    public Wallet toWalletAndSave(WalletRequest walletRequest) {
        Wallet wallet = new Wallet();
        wallet.setWalletName(walletRequest.walletName());
        wallet.setDescription(walletRequest.description());
        wallet.setWalletLimit(walletRequest.limit());
        wallet.setWalletPin(walletRequest.walletPin());
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);
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