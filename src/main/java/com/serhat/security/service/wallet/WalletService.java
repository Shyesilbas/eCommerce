package com.serhat.security.service.wallet;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
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
