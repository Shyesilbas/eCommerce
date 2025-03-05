package com.serhat.security.interfaces;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface WalletService {
    WalletCreatedResponse createWallet(HttpServletRequest request, WalletRequest walletRequest);
    WalletLimitUpdateResponse limitUpdate(HttpServletRequest servletRequest , BigDecimal newLimit);
    DepositSuccessfulResponse depositMoney(HttpServletRequest request, BigDecimal amount);
    Page<TransactionResponse> getTransactionHistory(HttpServletRequest request, Pageable pageable);
    Wallet getWalletByUser(User user);
    Wallet getUserAndTheirWallet(HttpServletRequest request);

    WalletInfoResponse walletInfo(HttpServletRequest request);
}
