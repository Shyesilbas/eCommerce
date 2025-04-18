package com.serhat.ecommerce.wallet.service;

import com.serhat.ecommerce.dto.request.WalletRequest;
import com.serhat.ecommerce.dto.response.*;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.wallet.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface WalletService {
    WalletCreatedResponse createWallet(WalletRequest walletRequest);
    WalletLimitUpdateResponse limitUpdate(BigDecimal newLimit);
    DepositSuccessfulResponse depositMoney( BigDecimal amount);
    Page<TransactionResponse> getTransactionHistory(Pageable pageable);
    Wallet getWalletByUser(User user);
    Wallet getUserAndTheirWallet();

    WalletInfoResponse walletInfo();
}
