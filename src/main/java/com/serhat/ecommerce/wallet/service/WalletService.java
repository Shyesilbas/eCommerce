package com.serhat.ecommerce.wallet.service;

import com.serhat.ecommerce.wallet.dto.WalletRequest;
import com.serhat.ecommerce.payment.dto.TransactionResponse;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.wallet.dto.DepositSuccessfulResponse;
import com.serhat.ecommerce.wallet.dto.WalletCreatedResponse;
import com.serhat.ecommerce.wallet.dto.WalletInfoResponse;
import com.serhat.ecommerce.wallet.dto.WalletLimitUpdateResponse;
import com.serhat.ecommerce.wallet.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface WalletService {
    WalletCreatedResponse createWallet(WalletRequest walletRequest);
    WalletLimitUpdateResponse limitUpdate(BigDecimal newLimit);
    DepositSuccessfulResponse depositMoney(BigDecimal amount);
    Page<TransactionResponse> getTransactionHistory(Pageable pageable);
    Wallet getWalletByUser(User user);
    Wallet getUserAndTheirWallet();

    WalletInfoResponse walletInfo();
}
